package com.aipa.ai.config;

import com.aipa.ai.replies.OpsReplyFormatter;
import com.aipa.ai.tools.PaymentTools;
import com.aipa.application.TransactionQueryService;
import com.aipa.domain.model.PaymentProvider;
import com.aipa.domain.model.PaymentTransaction;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides exactly one ChatModel bean.
 * <ul>
 * <li>stub mode (default): offline deterministic answers via tools</li>
 * <li>live mode: OpenAI when {@code aipa.ai.stub-enabled=false}</li>
 * </ul>
 */
@Configuration
public class AiModelConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AiModelConfiguration.class);
    private static final Pattern TX_REF = Pattern.compile("(TX\\d+)", Pattern.CASE_INSENSITIVE);

    @Bean
    @ConditionalOnProperty(name = "aipa.ai.stub-enabled", havingValue = "true")
    ChatModel paymentChatModel(
            PaymentTools paymentTools,
            TransactionQueryService transactionQueryService,
            OpsReplyFormatter replies) {
        log.warn("AIPA stub ChatModel ACTIVE (set aipa.ai.stub-enabled=false "
                + "+ OPENAI_API_KEY for live LangChain4j/OpenAI tool-calling)");
        return new DeterministicPaymentChatModel(
                paymentTools, transactionQueryService, replies);
    }

    @Bean
    @ConditionalOnProperty(name = "aipa.ai.stub-enabled", havingValue = "false")
    ChatModel paymentChatModelLive(
            @Value("${langchain4j.open-ai.chat-model.api-key}") String apiKey,
            @Value("${langchain4j.open-ai.chat-model.model-name}") String modelName,
            @Value("${langchain4j.open-ai.chat-model.temperature:0.1}") double temperature) {
        log.info("AIPA live OpenAI ChatModel ACTIVE ({})", modelName);
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature)
                .build();
    }

    static final class DeterministicPaymentChatModel implements ChatModel {

        private final PaymentTools paymentTools;
        private final TransactionQueryService transactionQueryService;
        private final OpsReplyFormatter replies;

        DeterministicPaymentChatModel(
                PaymentTools paymentTools,
                TransactionQueryService transactionQueryService,
                OpsReplyFormatter replies) {
            this.paymentTools = paymentTools;
            this.transactionQueryService = transactionQueryService;
            this.replies = replies;
        }

        @Override
        public ChatResponse doChat(ChatRequest chatRequest) {
            String question = extractLastUserText(chatRequest);
            String answer = answer(question);
            return ChatResponse.builder()
                    .aiMessage(AiMessage.from(answer))
                    .build();
        }

        private String extractLastUserText(ChatRequest request) {
            return UserMessage.findLast(request.messages())
                    .map(UserMessage::singleText)
                    .orElse("");
        }

        private String answer(String question) {
            String q = question.toLowerCase(Locale.ROOT);

            Matcher matcher = TX_REF.matcher(question);
            if (matcher.find()) {
                String ref = matcher.group(1).toUpperCase(Locale.ROOT);
                if (q.contains("pourquoi") || q.contains("échou") || q.contains("echec")
                        || q.contains("failed") || q.contains("erreur")) {
                    return paymentTools.explainTransactionFailure(ref);
                }
                return paymentTools.findTransaction(ref);
            }

            if ((q.contains("plusieurs") || q.contains("multiples"))
                    && (q.contains("échec") || q.contains("echec") || q.contains("fail"))) {
                return paymentTools.findCustomersWithMultipleFailures(2);
            }

            if (q.contains("combien") && (q.contains("échou") || q.contains("echec")
                    || q.contains("fail"))) {
                return paymentTools.findFailedTransactions(paymentTools.today());
            }

            if ((q.contains("orange") || q.contains("orange money"))
                    && (q.contains("50") || q.contains("supérieur")
                            || q.contains("superieur") || q.contains(">"))) {
                return paymentTools.findTransactionsByProviderAboveAmount(
                        "Orange Money", 50_000);
            }

            if (q.contains("wave") && (q.contains("échou") || q.contains("echec")
                    || q.contains("fail"))) {
                List<PaymentTransaction> failed = transactionQueryService.findFailedOn(
                        transactionQueryService.today()).stream()
                        .filter(tx -> tx.provider() == PaymentProvider.WAVE)
                        .toList();
                return replies.failedList(transactionQueryService.today(), failed);
            }

            if (q.contains("wave")) {
                return paymentTools.findTransactionsByProvider("Wave");
            }
            if (q.contains("orange")) {
                return paymentTools.findTransactionsByProvider("Orange Money");
            }
            if (q.contains("statistique") || q.contains("stats")) {
                return paymentTools.getStatistics();
            }
            if (q.contains("pending") || q.contains("en attente")) {
                return paymentTools.countTransactions("PENDING");
            }
            if (q.contains("échou") || q.contains("echec") || q.contains("fail")) {
                return paymentTools.findFailedTransactions(paymentTools.today());
            }

            return replies.help();
        }
    }
}

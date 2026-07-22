package com.aipa.api;

import com.aipa.ai.agent.PaymentSupportAgent;
import com.aipa.api.dto.ChatRequest;
import com.aipa.api.dto.ChatResponse;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/ai", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "AI Assistant")
@SecurityRequirement(name = "ApiKey")
public class AiChatController {

    private static final Logger log = LoggerFactory.getLogger(AiChatController.class);

    private final PaymentSupportAgent paymentSupportAgent;

    public AiChatController(PaymentSupportAgent paymentSupportAgent) {
        this.paymentSupportAgent = paymentSupportAgent;
    }

    @PostMapping(path = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Ask the payment operations assistant a natural-language question")
    @Observed(name = "aipa.ai.chat", contextualName = "ai-chat")
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        log.info("AI chat question received (length={})", request.question().length());
        String answer = paymentSupportAgent.chat(request.question());
        return new ChatResponse(answer);
    }
}

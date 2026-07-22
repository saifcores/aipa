package com.aipa.telemetry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class TelemetryConfig {

    private static final Logger log = LoggerFactory.getLogger(TelemetryConfig.class);

    @Bean
    ChatModelListener chatModelListener(MeterRegistry meterRegistry) {
        return new ChatModelListener() {
            @Override
            public void onRequest(ChatModelRequestContext requestContext) {
                log.debug("LLM request received");
                meterRegistry.counter("aipa.ai.requests").increment();
            }

            @Override
            public void onResponse(ChatModelResponseContext responseContext) {
                log.debug("LLM response received");
                meterRegistry.counter("aipa.ai.responses").increment();
            }

            @Override
            public void onError(ChatModelErrorContext errorContext) {
                log.warn("LLM error: {}", errorContext.error().getMessage());
                meterRegistry.counter("aipa.ai.errors").increment();
            }
        };
    }
}

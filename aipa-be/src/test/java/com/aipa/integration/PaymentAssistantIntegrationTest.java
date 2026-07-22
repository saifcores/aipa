package com.aipa.integration;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class PaymentAssistantIntegrationTest {

        private static final String API_KEY = "test-api-key";

        @Container
        static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
                        .withDatabaseName("aipa")
                        .withUsername("aipa")
                        .withPassword("aipa");

        @DynamicPropertySource
        static void datasourceProps(DynamicPropertyRegistry registry) {
                registry.add("spring.datasource.url", postgres::getJdbcUrl);
                registry.add("spring.datasource.username", postgres::getUsername);
                registry.add("spring.datasource.password", postgres::getPassword);
                registry.add("aipa.security.api-key", () -> API_KEY);
                registry.add("aipa.ai.stub-enabled", () -> "true");
                registry.add("management.otlp.tracing.export.enabled", () -> "false");
        }

        @Autowired
        private MockMvc mockMvc;

        @Test
        void listTransactions_requiresApiKey() throws Exception {
                mockMvc.perform(get("/transactions"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void listTransactions_returnsSeededData() throws Exception {
                mockMvc.perform(get("/transactions").header("X-API-Key", API_KEY))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].reference").exists());
        }

        @Test
        void aiChat_findsTransactionByReference() throws Exception {
                String body = """
                                {"question":"Où est la transaction TX45892 ?"}
                                """;

                String response = mockMvc.perform(post("/ai/chat")
                                .header("X-API-Key", API_KEY)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.answer").exists())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                assertThat(response).containsIgnoringCase("TX45892");
                assertThat(response).containsIgnoringCase("SUCCESS");
        }

        @Test
        void aiChat_explainsFailure() throws Exception {
                String body = """
                                {"question":"Pourquoi la transaction TX45893 a échoué ?"}
                                """;

                String response = mockMvc.perform(post("/ai/chat")
                                .header("X-API-Key", API_KEY)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                assertThat(response).contains("ERROR_105");
                assertThat(response).containsIgnoringCase("solde");
        }
}

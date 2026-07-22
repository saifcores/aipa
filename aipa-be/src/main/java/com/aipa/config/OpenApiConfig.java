package com.aipa.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        @Bean
        OpenAPI aipaOpenApi() {
                final String schemeName = "ApiKey";
                return new OpenAPI()
                                .info(new Info()
                                                .title("AI Payment Operations Assistant API")
                                                .description("""
                                                                Natural-language assistant for payment operations teams.
                                                                Read-only consultation, search and analysis over
                                                                transactional PostgreSQL data via LangChain4j tools.
                                                                """)
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("AIPA")
                                                                .email("ops@aipa.local")))
                                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                                .components(new Components().addSecuritySchemes(schemeName,
                                                new SecurityScheme()
                                                                .name("X-API-Key")
                                                                .type(SecurityScheme.Type.APIKEY)
                                                                .in(SecurityScheme.In.HEADER)));
        }
}

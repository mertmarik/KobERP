package com.koberp.sales.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "KobERP Sales Service API",
        version = "1.0",
        description = "Sales Management API with Auth0 JWT Authentication",
        contact = @Contact(
            name = "KobERP Team",
            email = "support@koberp.com"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8086", description = "Local Environment"),
        @Server(url = "https://api.koberp.com", description = "Production Environment")
    }
)
@SecurityScheme(
    name = "bearer-jwt",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Enter your Auth0 JWT token"
)
public class OpenApiConfig {
}

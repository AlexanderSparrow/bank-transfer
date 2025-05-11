package ru.pionerpixel.banktransfer.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Bank Transfer API",
                version = "1.0",
                description = "API для управления пользователями, балансами и переводами."
        )
)
public class OpenApiConfig {
}

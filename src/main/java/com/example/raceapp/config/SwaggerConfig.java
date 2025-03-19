package com.example.raceapp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Swagger API documentation.
 * This class sets up the OpenAPI configuration for the Race Application API.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Race App API",
                version = "1.0",
                description = "API documentation for Race Application"
        )
)
public class SwaggerConfig {

    /**
     * Configures and returns an OpenAPI instance for the Race App API.
     *
     * @return an OpenAPI instance configured with title, version,
     *          description, and contact information.
     */
    @Bean
    public OpenAPI raceAppApi() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Race App API")
                        .version("1.0")
                        .description("API documentation for Race Application")
                        .contact(new Contact()
                                .name("Your Name")
                                .email("your.email@example.com"))
                );
    }
}

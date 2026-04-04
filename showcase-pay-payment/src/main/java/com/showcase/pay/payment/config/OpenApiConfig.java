package com.showcase.pay.payment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for the payment service.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI paymentOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Showcase Pay - Payment Service API")
                        .description("RESTful API for payment processing, including payment creation, " +
                                "callback handling, refunds, and status queries.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Showcase Pay Team")
                                .email("support@showcase-pay.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}

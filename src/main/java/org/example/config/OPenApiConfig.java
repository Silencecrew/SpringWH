package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OPenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Person Service API")
                        .version("1.0")
                        .description("Документация")
                        .contact(new Contact()
                                .name("Команда разработки")
                                .email("vllladdikavkaz@gmail.com")));
    }
}

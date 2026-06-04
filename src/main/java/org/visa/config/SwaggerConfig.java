package org.visa.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI rewardsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rewards Points Ledger API")
                        .description("API for managing member reward points — earn, redeem, and track balances.")
                        .version("1.0"));
    }
}

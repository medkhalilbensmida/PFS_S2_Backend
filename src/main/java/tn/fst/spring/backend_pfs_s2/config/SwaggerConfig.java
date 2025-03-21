package tn.fst.spring.backend_pfs_s2.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gestion des surveillances dans le DSI")
                        .description("API pour la gestion des surveillances dans le DSI")
                        .version("1.0"));
    }
}
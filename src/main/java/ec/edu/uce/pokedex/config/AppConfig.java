package ec.edu.uce.pokedex.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    /**
     * Configuración del cliente WebClient para interactuar con la API de PokeAPI.
     *
     * @param builder WebClient.Builder proporcionado por Spring.
     * @return WebClient configurado con base URL y límite de memoria.
     */
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://pokeapi.co/api/v2")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10 MB
                .build();
    }
}

package ec.edu.uce.pokedex.config;

import ec.edu.uce.pokedex.view.MainView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl("https://pokeapi.co/api/v2").build();
    }
    @Bean
    public MainView mainView() {
        return new MainView();
    }
}
package ec.edu.uce.pokedex.service;

import ec.edu.uce.pokedex.model.Pokemon;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PokeService {
    private final WebClient webClient;

    public PokeService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Pokemon> getPokemonByName(String name) {
        return webClient.get()
                .uri("/pokemon/{name}", name)
                .retrieve()
                .bodyToMono(Pokemon.class);
    }
}

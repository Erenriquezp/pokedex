package ec.edu.uce.pokedex.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class TypeService {
    private final WebClient webClient;

    public TypeService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<String> getPokemonByType(String typeName) {
        return webClient.get()
                .uri("/type/{name}", typeName)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMapMany(response -> Flux.fromIterable((List<Map<String, Object>>) response.get("pokemon")))
                .map(pokemonData -> (String) ((Map<String, Object>) pokemonData.get("pokemon")).get("name"));
    }

    public Mono<Map> getTypeDetails(String typeName) {
        return webClient.get()
                .uri("/type/{name}", typeName)
                .retrieve()
                .bodyToMono(Map.class);
    }
}

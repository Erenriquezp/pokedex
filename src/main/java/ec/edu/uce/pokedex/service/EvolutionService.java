package ec.edu.uce.pokedex.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class EvolutionService {
    private final WebClient webClient;

    public EvolutionService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono getEvolutionChain(String speciesName) {
        return webClient.get()
                .uri("/pokemon-species/{name}", speciesName)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(speciesData -> {
                    Map<String, Object> evolutionChain = (Map<String, Object>) speciesData.get("evolution_chain");
                    String url = (String) evolutionChain.get("url");
                    return webClient.get().uri(url).retrieve().bodyToMono(Map.class);
                });
    }
}

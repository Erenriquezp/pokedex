package ec.edu.uce.pokedex.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EvolutionService {
    private final WebClient webClient;

    public EvolutionService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<List<Map<String, Object>>> getEvolutionChain(String speciesName) {
        return webClient.get()
                .uri("/pokemon-species/{name}", speciesName)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(speciesData -> {
                    Map<String, Object> evolutionChain = (Map<String, Object>) speciesData.get("evolution_chain");
                    String url = (String) evolutionChain.get("url");
                    return webClient.get().uri(url).retrieve().bodyToMono(Map.class);
                })
                .map(chainData -> extractEvolutionDetails((Map<String, Object>) chainData.get("chain"), new ArrayList<>()));
    }

    private List<Map<String, Object>> extractEvolutionDetails(Map<String, Object> chain, List<Map<String, Object>> evolutions) {
        evolutions.add(Map.of(
                "species", chain.get("species"),
                "evolution_details", chain.get("evolution_details")
        ));

        List<Map<String, Object>> evolvesTo = (List<Map<String, Object>>) chain.get("evolves_to");
        if (evolvesTo != null && !evolvesTo.isEmpty()) {
            for (Map<String, Object> nextEvolution : evolvesTo) {
                extractEvolutionDetails(nextEvolution, evolutions);
            }
        }
        return evolutions;
    }
}

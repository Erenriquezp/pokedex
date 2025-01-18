package ec.edu.uce.pokedex.service;

import ec.edu.uce.pokedex.models.Stat;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Service
public class StatService {
    private final WebClient webClient;

    public StatService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Stat> getStatsForPokemon(String pokemonName) {
        return webClient.get()
                .uri("/pokemon/{name}", pokemonName)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMapMany(response -> Flux.fromIterable((List<Map<String, Object>>) response.get("stats")))
                .map(statData -> {
                    Map<String, Object> stat = (Map<String, Object>) statData.get("stat");
                    return new Stat(
                            (int) statData.get("base_stat"),
                            (int) statData.get("effort"),
                            (String) stat.get("name")
                    );
                });
    }
}

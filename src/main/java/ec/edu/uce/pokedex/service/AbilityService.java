package ec.edu.uce.pokedex.service;

import ec.edu.uce.pokedex.models.Ability;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class AbilityService {
    private final WebClient webClient;

    public AbilityService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Ability> getAbilitiesForPokemon(String pokemonName) {
        return webClient.get()
                .uri("/pokemon/{name}", pokemonName)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMapMany(response -> Flux.fromIterable((List<Map<String, Object>>) response.get("abilities")))
                .map(abilityData -> {
                    Map<String, Object> ability = (Map<String, Object>) abilityData.get("ability");
                    return new Ability(
                            (String) ability.get("name"),
                            (String) ability.get("url"),
                            (boolean) abilityData.get("is_hidden"),
                            (int) abilityData.get("slot")
                    );
                });
    }

    public Mono<Ability> getAbilityDetails(String abilityName) {
        return webClient.get()
                .uri("/ability/{name}", abilityName)
                .retrieve()
                .bodyToMono(Ability.class);
    }
}

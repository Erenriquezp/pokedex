package ec.edu.uce.pokedex.service;

import ec.edu.uce.pokedex.models.Sprites;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class SpriteService {
    private final WebClient webClient;

    public SpriteService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Sprites> getSpritesForPokemon(String pokemonName) {
        return webClient.get()
                .uri("/pokemon/{name}", pokemonName)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    Map<String, Object> spritesData = (Map<String, Object>) response.get("sprites");
                    Sprites sprites = new Sprites();
                    sprites.setFrontDefault((String) spritesData.get("front_default"));
                    sprites.setBackDefault((String) spritesData.get("back_default"));
                    sprites.setFrontShiny((String) spritesData.get("front_shiny"));
                    sprites.setBackShiny((String) spritesData.get("back_shiny"));
                    return sprites;
                });
    }
}

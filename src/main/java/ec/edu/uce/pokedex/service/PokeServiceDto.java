package ec.edu.uce.pokedex.service;

import ec.edu.uce.pokedex.dto.PokemonDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class PokeServiceDto {
    private final WebClient webClient;

    public PokeServiceDto(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Fetches a simplified Pokémon DTO with selected fields.
     *
     * @param name Pokémon name.
     * @return Mono containing PokemonDto.
     */
    public Mono<PokemonDto> getPokemonDtoByName(String name) {
        return webClient.get()
                .uri("/pokemon/{name}", name)
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::mapToPokemonDto);
    }

    /**
     * Maps the API response to a PokemonDto.
     *
     * @param response API response as a Map.
     * @return PokemonDto object.
     */
    private PokemonDto mapToPokemonDto(Map<String, Object> response) {
        PokemonDto dto = new PokemonDto();

        dto.setName((String) response.get("name"));

        // Extract the first type
        List<Map<String, Object>> types = (List<Map<String, Object>>) response.get("types");
        if (!types.isEmpty()) {
            Map<String, Object> type = (Map<String, Object>) types.get(0).get("type");
            dto.setType((String) type.get("name"));
        }

        // Extract the first ability
        List<Map<String, Object>> abilities = (List<Map<String, Object>>) response.get("abilities");
        if (!abilities.isEmpty()) {
            Map<String, Object> ability = (Map<String, Object>) abilities.get(0).get("ability");
            dto.setAbility((String) ability.get("name"));
        }

        // Extract the front default sprite
        Map<String, Object> sprites = (Map<String, Object>) response.get("sprites");
        dto.setSpriteUrl((String) sprites.get("front_default"));

        return dto;
    }
}

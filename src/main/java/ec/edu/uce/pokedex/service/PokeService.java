package ec.edu.uce.pokedex.service;

import ec.edu.uce.pokedex.models.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class PokeService {
    private final WebClient webClient;

    public PokeService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Fetches and maps a Pokémon from the API response.
     *
     * @param name Name of the Pokémon.
     * @return Mono of Pokemon.
     */
    public Mono<Pokemon> getPokemonByName(String name) {
        return webClient.get()
                .uri("/pokemon/{name}", name)
                .retrieve()
                .bodyToMono(Map.class) // Obtener respuesta como Map para mapeo manual
                .map(this::mapToPokemon) // Mapear manualmente a la clase Pokemon
                .doOnNext(pokemon -> System.out.println("Pokemon Response: " + pokemon));
    }

    /**
     * Maps the API response to a Pokemon object.
     *
     * @param response The API response as a Map.
     * @return Pokemon object.
     */
    private Pokemon mapToPokemon(Map<String, Object> response) {
        Pokemon pokemon = new Pokemon();

        // Datos básicos
        pokemon.setId((int) response.get("id"));
        pokemon.setName((String) response.get("name"));
        pokemon.setBaseExperience((int) response.get("base_experience"));
        pokemon.setHeight((int) response.get("height"));
        pokemon.setWeight((int) response.get("weight"));
        pokemon.setOrder((int) response.get("order"));

        // Mapear tipos
        List<Map<String, Object>> types = (List<Map<String, Object>>) response.get("types");
        pokemon.setTypes(types.stream().map(typeData -> {
            Type type = new Type();
            Map<String, Object> typeInfo = (Map<String, Object>) typeData.get("type");
            type.setSlot((int) typeData.get("slot"));
            type.setName((String) typeInfo.get("name"));
            return type;
        }).toList());

        // Mapear habilidades
        List<Map<String, Object>> abilities = (List<Map<String, Object>>) response.get("abilities");
        pokemon.setAbilities(abilities.stream().map(abilityData -> {
            Ability ability = new Ability();
            Map<String, Object> abilityInfo = (Map<String, Object>) abilityData.get("ability");
            ability.setName((String) abilityInfo.get("name"));
            ability.setUrl((String) abilityInfo.get("url"));
            ability.setHidden((boolean) abilityData.get("is_hidden"));
            ability.setSlot((int) abilityData.get("slot"));
            return ability;
        }).toList());

        // Mapear sprites
        Map<String, Object> spritesData = (Map<String, Object>) response.get("sprites");
        Sprites sprites = new Sprites();
        sprites.setFrontDefault((String) spritesData.get("front_default"));
        sprites.setBackDefault((String) spritesData.get("back_default"));
        sprites.setFrontShiny((String) spritesData.get("front_shiny"));
        sprites.setBackShiny((String) spritesData.get("back_shiny"));
        pokemon.setSprites(sprites);

        // Devolver el objeto mapeado
        return pokemon;
    }
}

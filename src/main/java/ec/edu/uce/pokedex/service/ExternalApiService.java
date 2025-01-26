package ec.edu.uce.pokedex.service;

import ec.edu.uce.pokedex.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExternalApiService {

    private final WebClient webClient;

    /**
     * Obtiene los datos de un Pokémon desde la API externa.
     *
     * @param name Nombre del Pokémon.
     * @return Objeto Pokemon mapeado desde la API.
     */
    public Mono<Pokemon> getPokemonFromApi(String name) {
        return webClient.get()
                .uri("/pokemon/{name}", name.toLowerCase())
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::mapToPokemon);
    }

    /**
     * Obtiene todos los Pokémon de la API externa.
     *
     * @param limit  Límite de Pokémon a obtener.
     * @param offset Punto de inicio para obtener los Pokémon.
     * @return Lista de objetos Pokémon mapeados.
     */
    public Flux<Pokemon> getAllPokemonFromApi(int limit, int offset) {
        return webClient.get()
                .uri("/pokemon?limit={limit}&offset={offset}", limit, offset)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMapMany(data -> Flux.fromIterable((List<Map<String, Object>>) data.get("results")))
                .flatMap(result -> getPokemonFromApi((String) result.get("name")));
    }

    /**
     * Mapea los datos obtenidos de la API a un objeto Pokemon.
     */
    private Pokemon mapToPokemon(Map<String, Object> data) {
        Pokemon pokemon = new Pokemon();
        pokemon.setId((int) data.get("id"));
        pokemon.setName((String) data.get("name"));
        pokemon.setBaseExperience((int) data.get("base_experience"));
        pokemon.setHeight((int) data.get("height"));
        pokemon.setWeight((int) data.get("weight"));
        pokemon.setOrderIndex((int) data.get("order"));
        pokemon.setAbilities(mapList(data, "abilities", this::mapToAbility));
        pokemon.setStats(mapList(data, "stats", this::mapToStat));
        pokemon.setTypes(mapList(data, "types", this::mapToType));
        pokemon.setMoves(mapList(data, "moves", this::mapToMove));
        pokemon.setSprites(mapToSprites((Map<String, Object>) data.get("sprites")));
        return pokemon;
    }

    /**
     * Mapea un listado general a una lista de objetos.
     */
    private <T> List<T> mapList(Map<String, Object> data, String key, java.util.function.Function<Map<String, Object>, T> mapper) {
        return ((List<Map<String, Object>>) data.get(key)).stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    private Ability mapToAbility(Map<String, Object> data) {
        Map<String, Object> abilityData = (Map<String, Object>) data.get("ability");
        return new Ability(
                (String) abilityData.get("name"),
                (String) abilityData.get("url"),
                (boolean) data.get("is_hidden"),
                (int) data.get("slot")
        );
    }

    private Stat mapToStat(Map<String, Object> data) {
        Map<String, Object> statData = (Map<String, Object>) data.get("stat");
        return new Stat(
                (int) data.get("base_stat"),
                (int) data.get("effort"),
                (String) statData.get("name")
        );
    }

    private Type mapToType(Map<String, Object> data) {
        Map<String, Object> typeData = (Map<String, Object>) data.get("type");
        return new Type(
                (int) data.get("slot"),
                (String) typeData.get("name"),
                (String) typeData.get("url")
        );
    }

    private Move mapToMove(Map<String, Object> data) {
        Map<String, Object> moveData = (Map<String, Object>) data.get("move");
        return new Move((String) moveData.get("name"), (String) moveData.get("url"));
    }

    private Sprites mapToSprites(Map<String, Object> data) {
        Sprites sprites = new Sprites();
        sprites.setFrontDefault((String) data.get("front_default"));
        sprites.setBackDefault((String) data.get("back_default"));
        sprites.setFrontShiny((String) data.get("front_shiny"));
        sprites.setBackShiny((String) data.get("back_shiny"));
        sprites.setFrontFemale((String) data.get("front_female"));
        sprites.setBackFemale((String) data.get("back_female"));
        sprites.setFrontShinyFemale((String) data.get("front_shiny_female"));
        sprites.setBackShinyFemale((String) data.get("back_shiny_female"));
        return sprites;
    }
}

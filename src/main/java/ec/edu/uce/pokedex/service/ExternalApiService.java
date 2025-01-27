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
     * <p>
     * Este método realiza una petición a la API externa para obtener los datos
     * completos de un Pokémon específico utilizando su nombre. Luego, mapea
     * la respuesta a un objeto `Pokemon` con la información correspondiente.
     *
     * @param name Nombre del Pokémon a consultar.
     * @return Un objeto `Mono<Pokemon>` que contiene el Pokémon mapeado.
     */
    public Mono<Pokemon> getPokemonFromApi(String name) {
        return webClient.get()
                .uri("/pokemon/{name}", name.toLowerCase()) // Se utiliza el nombre del Pokémon en minúsculas
                .retrieve()
                .bodyToMono(Map.class) // Se convierte la respuesta en un mapa
                .map(this::mapToPokemon); // Mapea el mapa a un objeto Pokémon
    }

    /**
     * Obtiene todos los Pokémon de la API externa.
     * <p>
     * Este método realiza una petición a la API externa para obtener una lista
     * de Pokémon. La respuesta se procesa y mapea a objetos `Pokemon` y se devuelve
     * como un flujo (`Flux`).
     *
     * @param limit  Límite de Pokémon a obtener.
     * @param offset Punto de inicio para obtener los Pokémon.
     * @return Un objeto `Flux<Pokemon>` que contiene los Pokémon mapeados.
     */
    public Flux<Pokemon> getAllPokemonFromApi(int limit, int offset) {
        return webClient.get()
                .uri("/pokemon?limit={limit}&offset={offset}", limit, offset) // Obtiene una lista de Pokémon
                .retrieve()
                .bodyToMono(Map.class) // Convierte la respuesta a un mapa
                .flatMapMany(data -> Flux.fromIterable((List<Map<String, Object>>) data.get("results"))) // Extrae los resultados
                .flatMap(result -> getPokemonFromApi((String) result.get("name"))); // Obtiene detalles de cada Pokémon
    }


    /**
     * Mapea los datos obtenidos de la API a un objeto `Pokemon`.
     * <p>
     * Este método toma un mapa con los datos crudos obtenidos de la API externa
     * y mapea esos datos a un objeto `Pokemon`. Este proceso incluye la conversión
     * de las habilidades, estadísticas, tipos y movimientos a objetos específicos.
     *
     * @param data Mapa de datos crudos obtenidos de la API externa.
     * @return Un objeto `Pokemon` con los datos mapeados.
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
     * Mapea un listado general de datos a una lista de objetos específicos.
     * <p>
     * Este método recibe un listado de datos crudos y los mapea utilizando
     * una función `mapper` que define cómo convertir cada elemento del listado
     * en un objeto de tipo `T`.
     *
     * @param data   El mapa que contiene los datos crudos.
     * @param key    La clave que contiene el listado de elementos en el mapa.
     * @param mapper La función que mapea cada elemento del listado a un objeto.
     * @param <T>    El tipo de objeto al que se mapea cada elemento.
     * @return Una lista de objetos mapeados de tipo `T`.
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

package ec.edu.uce.pokedex.service;

import ec.edu.uce.pokedex.models.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExternalApiService {

    private final WebClient webClient;

    public ExternalApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Obtiene los datos de un Pokémon desde la API externa.
     *
     * @param name Nombre del Pokémon.
     * @return Objeto Pokemon mapeado desde la API.
     */
    public Pokemon getPokemonFromApi(String name) {
        return webClient.get()
                .uri("/pokemon/{name}", name.toLowerCase())
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::mapToPokemon)
                .block(); // Bloqueo para simplificar el flujo y obtener el resultado directamente
    }

    /**
     * Obtiene todos los Pokémon de la API externa.
     *
     * @param limit  Límite de Pokémon a obtener.
     * @param offset Punto de inicio para obtener los Pokémon.
     * @return Lista de objetos Pokémon mapeados.
     */
    public List<Pokemon> getAllPokemonFromApi(int limit, int offset) {
        // Realizar la solicitud inicial para obtener nombres de Pokémon
        List<String> pokemonNames = webClient.get()
                .uri("/pokemon?limit={limit}&offset={offset}", limit, offset)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMapIterable(data -> (List<Map<String, Object>>) data.get("results"))
                .map(result -> (String) result.get("name"))
                .collect(Collectors.toList())
                .block();

        // Obtener detalles de cada Pokémon y mapearlos
        assert pokemonNames != null;
        return pokemonNames.stream()
                .map(this::getPokemonFromApi)
                .collect(Collectors.toList());
    }

    /**
     * Mapea los datos obtenidos de la API a un objeto Pokemon.
     *
     * @param data Datos en formato Map.
     * @return Objeto Pokemon.
     */
    private Pokemon mapToPokemon(Map<String, Object> data) {
//        System.out.println("data = " + data);
        Pokemon pokemon = new Pokemon();
        pokemon.setId((int) data.get("id"));
        pokemon.setName((String) data.get("name"));
        pokemon.setBaseExperience((int) data.get("base_experience"));
        pokemon.setHeight((int) data.get("height"));
        pokemon.setWeight((int) data.get("weight"));
        pokemon.setOrderIndex((int) data.get("order"));

        // Mapear habilidades
        List<Ability> abilities = ((List<Map<String, Object>>) data.get("abilities")).stream()
                .map(this::mapToAbility)
                .collect(Collectors.toList());
        pokemon.setAbilities(abilities);

        // Mapear estadísticas
        List<Stat> stats = ((List<Map<String, Object>>) data.get("stats")).stream()
                .map(this::mapToStat)
                .collect(Collectors.toList());
        pokemon.setStats(stats);

        // Mapear tipos
        List<Type> types = ((List<Map<String, Object>>) data.get("types")).stream()
                .map(this::mapToType)
                .collect(Collectors.toList());
        pokemon.setTypes(types);

        // Mapear movimientos
        List<Move> moves = ((List<Map<String, Object>>) data.get("moves")).stream()
                .map(this::mapToMove)
                .collect(Collectors.toList());
        pokemon.setMoves(moves);

        // Mapear sprites
        Sprites sprites = mapToSprites((Map<String, Object>) data.get("sprites"));
        pokemon.setSprites(sprites);

        return pokemon;
    }

    /**
     * Mapea los datos de habilidades de la API al modelo Ability.
     *
     * @param data Datos de la habilidad.
     * @return Objeto Ability.
     */
    private Ability mapToAbility(Map<String, Object> data) {
        Map<String, Object> abilityData = (Map<String, Object>) data.get("ability");
        return new Ability(
                (String) abilityData.get("name"),
                (String) abilityData.get("url"),
                (boolean) data.get("is_hidden"),
                (int) data.get("slot")
        );
    }

    /**
     * Mapea los datos de estadísticas de la API al modelo Stat.
     *
     * @param data Datos de la estadística.
     * @return Objeto Stat.
     */
    private Stat mapToStat(Map<String, Object> data) {
        Map<String, Object> statData = (Map<String, Object>) data.get("stat");
        return new Stat(
                (int) data.get("base_stat"),
                (int) data.get("effort"),
                (String) statData.get("name")
        );
    }

    /**
     * Mapea los datos de tipos de la API al modelo Type.
     *
     * @param data Datos del tipo.
     * @return Objeto Type.
     */
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

        // Crear y devolver el objeto Move
        Move move = new Move();
        move.setName((String) moveData.get("name"));
        move.setUrl((String) moveData.get("url"));

        return move;
    }

    /**
     * Mapea los datos de sprites de la API al modelo Sprites.
     *
     * @param data Datos de los sprites.
     * @return Objeto Sprites.
     */
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

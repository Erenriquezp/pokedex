package ec.edu.uce.pokedex.service;

import ec.edu.uce.pokedex.models.*;
import ec.edu.uce.pokedex.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PokeService {

    private final PokemonRepository pokemonRepository;
    private final AbilityRepository abilityRepository;
    private final StatRepository statRepository;
    private final TypeRepository typeRepository;
    private final MoveRepository moveRepository;
    private final ExternalApiService externalApiService;

    /**
     * Obtiene un Pokémon por su nombre desde la base de datos.
     *
     * @param name Nombre del Pokémon.
     * @return Pokémon si existe en la base de datos.
     */
    @Transactional(readOnly = true)
    public Optional<Pokemon> getPokemonByName(String name) {
        return Optional.ofNullable(pokemonRepository.findByNameIgnoreCase(name));
    }

    /**
     * Guarda un Pokémon en la base de datos.
     *
     * @param pokemon Entidad Pokémon a guardar.
     * @return Pokémon guardado.
     */
    @Transactional
    public Pokemon savePokemon(Pokemon pokemon) {
        return pokemonRepository.save(pokemon);
    }

    /**
     * Obtiene todos los Pokémon de la base de datos.
     *
     * @return Lista de Pokémon.
     */
    @Transactional(readOnly = true)
    public List<Pokemon> getAllPokemons() {
        return pokemonRepository.findAll();
    }

    /**
     * Carga un Pokémon desde la API externa y lo guarda en la base de datos.
     *
     * @param name Nombre del Pokémon a cargar.
     * @return Pokémon cargado y guardado.
     */
    @Transactional
    public Pokemon loadPokemonFromApiAndSave(String name) {
        Pokemon pokemon = externalApiService.getPokemonFromApi(name);
        savePokemonData(pokemon);
        return pokemon;
    }

    /**
     * Carga todos los Pokémon desde la API externa y los guarda en la base de datos.
     *
     * @param limit  Número máximo de Pokémon a cargar.
     * @param offset Punto de inicio para cargar los Pokémon.
     */
    @Transactional
    public void loadAllPokemonsFromApiAndSave(int limit, int offset) {
        List<Pokemon> pokemons = externalApiService.getAllPokemonFromApi(limit, offset);
        pokemons.forEach(this::savePokemonData);
    }

    /**
     * Carga y guarda en la base de datos la información asociada a un Pokémon.
     *
     * @param pokemon Pokémon a procesar.
     */
    @Transactional
    public void savePokemonData(Pokemon pokemon) {
        // Guardar el Pokémon
        Pokemon savedPokemon = pokemonRepository.save(pokemon);

        // Guardar las habilidades
        pokemon.getAbilities().forEach(ability -> {
            ability.setPokemon(savedPokemon);
            abilityRepository.save(ability);
        });

        // Guardar las estadísticas
        pokemon.getStats().forEach(stat -> {
            stat.setPokemon(savedPokemon);
            statRepository.save(stat);
        });

        // Guardar los tipos
        pokemon.getTypes().forEach(type -> {
            type.setPokemon(savedPokemon);
            typeRepository.save(type);
        });

        // Guardar los movimientos
        pokemon.getMoves().forEach(move -> {
            move.setPokemon(savedPokemon);
            moveRepository.save(move);
        });
    }
}
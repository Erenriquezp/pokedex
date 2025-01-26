package ec.edu.uce.pokedex.service;

import ec.edu.uce.pokedex.models.*;
import ec.edu.uce.pokedex.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PokeService {

    private final PokemonRepository pokemonRepository;
    private final ExternalApiService externalApiService;

    /**
     * Carga todos los Pokémon desde la API externa y los guarda en la base de datos.
     */
    @Transactional
    public void loadAllPokemonsFromApiAndSave(int limit, int offset) {
        externalApiService.getAllPokemonFromApi(limit, offset)
                .doOnNext(this::savePokemonData)
                .subscribe();
    }

    /**
     * Guarda un Pokémon y sus datos relacionados.
     */
    @Transactional
    public void savePokemonData(Pokemon pokemon) {
        pokemonRepository.save(pokemon);
    }

    public Optional<Pokemon> getPokemonByName(String lowerCase) {
        return Optional.ofNullable(pokemonRepository.findByNameIgnoreCase(lowerCase));
    }
}

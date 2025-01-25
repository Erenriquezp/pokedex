package ec.edu.uce.pokedex.service;

import ec.edu.uce.pokedex.models.Pokemon;
import ec.edu.uce.pokedex.models.Sprites;
import ec.edu.uce.pokedex.repository.SpritesRepository;
import org.springframework.stereotype.Service;

@Service
public class SpriteService {

    private final SpritesRepository spritesRepository;
    private final ExternalApiService externalApiService;

    public SpriteService(SpritesRepository spritesRepository, ExternalApiService externalApiService) {
        this.spritesRepository = spritesRepository;
        this.externalApiService = externalApiService;
    }

    /**
     * Retrieves sprites for a Pokémon from the database or loads them from the API if not found.
     *
     * @param pokemonName Name of the Pokémon.
     * @return Sprites object for the Pokémon.
     */
    public Sprites getSpritesForPokemon(String pokemonName) {
        Sprites sprites = spritesRepository.findByPokemonName(pokemonName);
        if (sprites != null) {
            return sprites;
        }

        Pokemon pokemon = externalApiService.getPokemonFromApi(pokemonName);
        Sprites fetchedSprites = pokemon.getSprites();
        if (fetchedSprites != null) {
            fetchedSprites.setPokemon(pokemon);
            spritesRepository.save(fetchedSprites);
        }
        return fetchedSprites;
    }
}

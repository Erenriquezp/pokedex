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
     * Recupera los sprites de un Pokémon de la base de datos o los carga desde la API si no se encuentran.
     *
     * @param pokemonName Nombre del Pokémon.
     * @return Objeto Sprites del Pokémon.
     */
    public Sprites getSpritesForPokemon(String pokemonName) {
        Sprites sprites = spritesRepository.findByPokemonName(pokemonName);
        if (sprites != null) {
            return sprites;
        }

        Pokemon pokemon = externalApiService.getPokemonFromApi(pokemonName).block();
        assert pokemon != null;
        Sprites fetchedSprites = pokemon.getSprites();
        if (fetchedSprites != null) {
            fetchedSprites.setPokemon(pokemon);
            spritesRepository.save(fetchedSprites);
        }
        return fetchedSprites;
    }
}

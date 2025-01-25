package ec.edu.uce.pokedex.controller;

import ec.edu.uce.pokedex.models.Sprites;
import ec.edu.uce.pokedex.service.SpriteService;
import org.springframework.stereotype.Controller;

@Controller
public class SpriteController {

    private final SpriteService spriteService;

    public SpriteController(SpriteService spriteService) {
        this.spriteService = spriteService;
    }

    /**
     * Retrieves sprites for a Pokémon by its name.
     *
     * @param pokemonName Name of the Pokémon.
     * @return Sprites object.
     */
    public Sprites getSpritesForPokemon(String pokemonName) {
        return spriteService.getSpritesForPokemon(pokemonName);
    }
}

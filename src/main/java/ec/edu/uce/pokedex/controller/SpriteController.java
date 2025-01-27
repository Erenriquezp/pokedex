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
     * Obtiene sprites de un Pokémon por su nombre.
     *
     * @param pokemonName Nombre del Pokémon.
     * @return Objeto Sprites.
     */
    public Sprites getSpritesForPokemon(String pokemonName) {
        return spriteService.getSpritesForPokemon(pokemonName);
    }
}

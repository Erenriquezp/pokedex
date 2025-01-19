package ec.edu.uce.pokedex.controller;

import ec.edu.uce.pokedex.models.Sprites;
import ec.edu.uce.pokedex.service.SpriteService;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
public class SpriteController {

    private final SpriteService spriteService;

    public SpriteController(SpriteService spriteService) {
        this.spriteService = spriteService;
    }

    /**
     * Fetches the sprites for a given Pokémon name.
     *
     * @param pokemonName Name of the Pokémon.
     * @return Mono containing the sprites.
     */
    public Mono<Sprites> getSpritesForPokemon(String pokemonName) {
        return spriteService.getSpritesForPokemon(pokemonName);
    }
}

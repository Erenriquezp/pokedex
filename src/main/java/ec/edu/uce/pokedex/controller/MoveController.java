package ec.edu.uce.pokedex.controller;

import ec.edu.uce.pokedex.models.Move;
import ec.edu.uce.pokedex.service.MoveService;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
public class MoveController {

    private final MoveService moveService;

    public MoveController(MoveService moveService) {
        this.moveService = moveService;
    }

    /**
     * Fetches the moves for a given Pokémon name.
     *
     * @param pokemonName Name of the Pokémon.
     * @return Flux of Move objects.
     */
    public Flux<Move> getMovesForPokemon(String pokemonName) {
        return moveService.getMovesForPokemon(pokemonName);
    }
}

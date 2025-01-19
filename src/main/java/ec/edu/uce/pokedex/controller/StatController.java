package ec.edu.uce.pokedex.controller;

import ec.edu.uce.pokedex.models.Stat;
import ec.edu.uce.pokedex.service.StatService;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
public class StatController {

    private final StatService statService;

    public StatController(StatService statService) {
        this.statService = statService;
    }

    /**
     * Retrieves the stats for a given Pokémon name.
     *
     * @param pokemonName Name of the Pokémon.
     * @return Flux of Stat objects.
     */
    public Flux<Stat> getStatsForPokemon(String pokemonName) {
        return statService.getStatsForPokemon(pokemonName);
    }
}

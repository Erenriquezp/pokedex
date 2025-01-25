package ec.edu.uce.pokedex.controller;

import ec.edu.uce.pokedex.models.Stat;
import ec.edu.uce.pokedex.service.StatService;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class StatController {

    private final StatService statService;

    public StatController(StatService statService) {
        this.statService = statService;
    }

    /**
     * Retrieves stats for a Pokémon from the database.
     *
     * @param pokemonName Name of the Pokémon.
     * @return List of stats.
     */
    public List<Stat> getStatsForPokemon(String pokemonName) {
        return statService.getStatsForPokemon(pokemonName);
    }
}

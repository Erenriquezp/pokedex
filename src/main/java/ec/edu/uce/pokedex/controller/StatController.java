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
     * Obtiene las estadísticas de un Pokémon de la base de datos.
     *
     * @param pokemonName Nombre del Pokémon.
     * @return Lista de estadísticas.
     */
    public List<Stat> getStatsForPokemon(String pokemonName) {
        return statService.getStatsForPokemon(pokemonName);
    }
}

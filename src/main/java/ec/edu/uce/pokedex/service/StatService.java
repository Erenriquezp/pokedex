package ec.edu.uce.pokedex.service;

import ec.edu.uce.pokedex.models.Stat;
import ec.edu.uce.pokedex.repository.StatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatService {
    private final StatRepository statRepository;

    public StatService(StatRepository statRepository) {
        this.statRepository = statRepository;
    }

    /**
     * Retrieves the stats for a Pokémon from the database.
     *
     * @param pokemonName Name of the Pokémon.
     * @return List of stats.
     */
    public List<Stat> getStatsForPokemon(String pokemonName) {
        return statRepository.findByPokemonName(pokemonName);
    }
}

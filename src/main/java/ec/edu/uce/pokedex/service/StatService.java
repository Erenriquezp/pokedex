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
     * Obtiene las estadísticas de un Pokémon de la base de datos.
     *
     * @param pokemonName Nombre del Pokémon.
     * @return Lista de estadísticas.
     */
    public List<Stat> getStatsForPokemon(String pokemonName) {
        return statRepository.findByPokemonName(pokemonName);
    }
}

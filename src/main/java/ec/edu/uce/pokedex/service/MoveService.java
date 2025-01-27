package ec.edu.uce.pokedex.service;

import ec.edu.uce.pokedex.models.Move;
import ec.edu.uce.pokedex.repository.MoveRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoveService {

    private final MoveRepository moveRepository;

    public MoveService(MoveRepository moveRepository) {
        this.moveRepository = moveRepository;
    }

    /**
     * Recupera todos los movimientos de un Pokémon de la base de datos.
     *
     * @param pokemonName Nombre del Pokémon.
     * @return Lista de objetos de movimiento.
     */
    public List<Move> getMovesForPokemon(String pokemonName) {
        return moveRepository.findByPokemonName(pokemonName);
    }
}

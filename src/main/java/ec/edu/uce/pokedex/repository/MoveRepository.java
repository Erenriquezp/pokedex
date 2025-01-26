package ec.edu.uce.pokedex.repository;

import ec.edu.uce.pokedex.models.Move;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoveRepository extends JpaRepository<Move, Long> {
    List<Move> findByPokemonName(String name);
}

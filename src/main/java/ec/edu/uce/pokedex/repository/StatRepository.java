package ec.edu.uce.pokedex.repository;

import ec.edu.uce.pokedex.models.Stat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<Stat, Long> {
    // Buscar estadísticas por Pokémon ID
    List<Stat> findByPokemonName(String name);
}

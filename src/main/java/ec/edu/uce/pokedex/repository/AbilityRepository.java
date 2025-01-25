package ec.edu.uce.pokedex.repository;

import ec.edu.uce.pokedex.models.Ability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbilityRepository extends JpaRepository<Ability, Long> {
    // Buscar habilidades por Pokémon ID
    List<Ability> findByPokemonId(Integer pokemonId);
}

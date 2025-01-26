package ec.edu.uce.pokedex.repository;

import ec.edu.uce.pokedex.models.Pokemon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Integer> {
    // Buscar Pokémon por nombre (case-insensitive)
    Pokemon findByNameIgnoreCase(String name);

    @Query("SELECT p FROM Pokemon p ORDER BY p.id ASC")
    List<Pokemon> findPagedPokemons(@Param("offset") int offset, @Param("limit") int limit);

    // Método para obtener Pokémon paginados
    Page<Pokemon> findAll(Pageable pageable);

}

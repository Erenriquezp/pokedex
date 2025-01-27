package ec.edu.uce.pokedex.repository;

import ec.edu.uce.pokedex.models.Pokemon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Integer> {
    // Buscar Pokémon por nombre (case-insensitive)
    Pokemon findByNameIgnoreCase(String name);

    // Método para obtener Pokémon paginados
    Page<Pokemon> findAll(Pageable pageable);

}

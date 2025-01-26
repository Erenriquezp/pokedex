package ec.edu.uce.pokedex.repository;

import ec.edu.uce.pokedex.models.Sprites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpritesRepository extends JpaRepository<Sprites, Long> {
    // Agregar consultas personalizadas si es necesario
    Sprites findByPokemonName(String name);

}

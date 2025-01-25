package ec.edu.uce.pokedex.repository;

import ec.edu.uce.pokedex.models.Sprites;
import ec.edu.uce.pokedex.models.Stat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpritesRepository extends JpaRepository<Sprites, Long> {
    // Agregar consultas personalizadas si es necesario
    Sprites findByPokemonName(String name);

}

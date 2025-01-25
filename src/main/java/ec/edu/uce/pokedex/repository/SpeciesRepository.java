package ec.edu.uce.pokedex.repository;

import ec.edu.uce.pokedex.models.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpeciesRepository extends JpaRepository<Species, Long> {
    // Buscar especie por nombre
    Species findByName(String name);
}

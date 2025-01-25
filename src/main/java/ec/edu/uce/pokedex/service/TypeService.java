package ec.edu.uce.pokedex.service;

import ec.edu.uce.pokedex.models.Pokemon;
import ec.edu.uce.pokedex.repository.TypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeService {

    private final TypeRepository typeRepository;

    public TypeService(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    /**
     * Busca todos los Pokémon asociados a un tipo.
     *
     * @param typeName Nombre del tipo.
     * @return Lista de nombres de Pokémon.
     */
    public List<Pokemon> getPokemonByType(String typeName) {
        return typeRepository.findPokemonsByTypeName(typeName);
    }
}

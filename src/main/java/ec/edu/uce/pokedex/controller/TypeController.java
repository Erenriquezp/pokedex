package ec.edu.uce.pokedex.controller;

import ec.edu.uce.pokedex.models.Pokemon;
import ec.edu.uce.pokedex.service.TypeService;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class TypeController {

    private final TypeService typeService;

    public TypeController(TypeService typeService) {
        this.typeService = typeService;
    }

    /**
     * Busca todos los Pokémon asociados a un tipo.
     *
     * @param typeName Nombre del tipo.
     * @return Lista de nombres de Pokémon.
     */
    public List<Pokemon> getPokemonByType(String typeName) {
        return typeService.getPokemonByType(typeName);
    }
}

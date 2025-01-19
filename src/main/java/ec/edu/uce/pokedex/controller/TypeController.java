package ec.edu.uce.pokedex.controller;

import ec.edu.uce.pokedex.service.TypeService;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
public class TypeController {

    private final TypeService typeService;

    public TypeController(TypeService typeService) {
        this.typeService = typeService;
    }

    /**
     * Retrieves Pokémon names associated with a given type.
     *
     * @param typeName Name of the type.
     * @return Flux of Pokémon names.
     */
    public Flux<String> getPokemonByType(String typeName) {
        return typeService.getPokemonByType(typeName);
    }

    /**
     * Retrieves detailed information about a type.
     *
     * @param typeName Name of the type.
     * @return Mono containing type details as a Map.
     */
    public Mono<Map> getTypeDetails(String typeName) {
        return typeService.getTypeDetails(typeName);
    }
}

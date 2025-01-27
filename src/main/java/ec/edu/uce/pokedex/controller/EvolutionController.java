package ec.edu.uce.pokedex.controller;

import ec.edu.uce.pokedex.service.EvolutionService;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Controller
public class EvolutionController {

    private final EvolutionService evolutionService;

    public EvolutionController(EvolutionService evolutionService) {
        this.evolutionService = evolutionService;
    }

    /**
     * Recupera la cadena de evolución de una especie de Pokémon determinada.
     *
     * @paramspeciesName Nombre de la especie de Pokémon.
     * @return Mono que contiene la cadena de evolución como una lista de etapas.
     */
    public Mono<List<Map<String, Object>>> getEvolutionChain(String speciesName) {
        return evolutionService.getEvolutionChain(speciesName);
    }
}

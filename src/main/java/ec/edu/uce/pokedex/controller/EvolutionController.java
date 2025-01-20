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
     * Retrieves the evolution chain for a given Pokémon species.
     *
     * @param speciesName Name of the Pokémon species.
     * @return Mono containing the evolution chain as a list of stages.
     */
    public Mono<List<Map<String, Object>>> getEvolutionChain(String speciesName) {
        return evolutionService.getEvolutionChain(speciesName);
    }
}

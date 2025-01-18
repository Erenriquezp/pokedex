package ec.edu.uce.pokedex.controller;

import ec.edu.uce.pokedex.models.Ability;
import ec.edu.uce.pokedex.service.AbilityService;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
public class AbilityController {

    private final AbilityService abilityService;

    public AbilityController(AbilityService abilityService) {
        this.abilityService = abilityService;
    }

    /**
     * Fetches the list of abilities for a given Pokémon name.
     *
     * @param pokemonName Name of the Pokémon.
     * @return Flux of abilities.
     */
    public Flux<Ability> getAbilitiesForPokemon(String pokemonName) {
        return abilityService.getAbilitiesForPokemon(pokemonName);
    }
}

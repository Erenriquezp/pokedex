package ec.edu.uce.pokedex.service;

import ec.edu.uce.pokedex.dto.PokemonDto;
import ec.edu.uce.pokedex.models.*;
import ec.edu.uce.pokedex.repository.PokemonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PokeServiceDto {

    private final PokemonRepository pokemonRepository;

    /**
     * Maps a Pokémon entity to a Pokémon DTO.
     *
     * @param pokemon Pokémon entity.
     * @return PokemonDto object.
     */
    private PokemonDto mapToPokemonDto(Pokemon pokemon) {
        return PokemonDto.builder()
                .name(pokemon.getName())
                .spriteUrl(pokemon.getSprites().getFrontDefault())
                .types(pokemon.getTypes().stream()
                        .map(type -> new Type(type.getSlot(), type.getName(), type.getUrl()))
                        .collect(Collectors.toList()))
                .abilities(pokemon.getAbilities().stream()
                        .map(ability -> new Ability(ability.getName(), ability.getUrl(), ability.isHidden(), ability.getSlot()))
                        .collect(Collectors.toList()))
                .build();
    }

    @Cacheable("pokemons")
    public List<PokemonDto> getPokemonPage(int offset, int limit) {
        Page<PokemonDto> pokemonPage = pokemonRepository.findAll(PageRequest.of(offset, limit))
                .map(this::mapToPokemonDto);
        return pokemonPage.getContent();
    }

}

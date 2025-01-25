package ec.edu.uce.pokedex.service;

import ec.edu.uce.pokedex.dto.PokemonDto;
import ec.edu.uce.pokedex.models.*;
import ec.edu.uce.pokedex.repository.PokemonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PokeServiceDto {

    private final PokemonRepository pokemonRepository;

    /**
     * Fetches a simplified Pokémon DTO with selected fields from the database.
     *
     * @param name Pokémon name.
     * @return Optional containing PokemonDto, empty if the Pokémon does not exist.
     */
    public Optional<PokemonDto> getPokemonDtoByName(String name) {
        // Buscar el Pokémon por nombre en la base de datos
        Optional<Pokemon> pokemonOptional = Optional.ofNullable(pokemonRepository.findByNameIgnoreCase(name));

        // Si el Pokémon existe, mapear al DTO
        return pokemonOptional.map(this::mapToPokemonDto);
    }

    /**
     * Maps a Pokémon entity to a Pokémon DTO.
     *
     * @param pokemon Pokémon entity.
     * @return PokemonDto object.
     */
    private PokemonDto mapToPokemonDto(Pokemon pokemon) {
        PokemonDto dto = new PokemonDto();

        // Mapear datos básicos
        dto.setName(pokemon.getName());
        dto.setSpriteUrl(pokemon.getSprites().getFrontDefault());

        // Mapear tipos
        dto.setTypes(
                pokemon.getTypes().stream()
                        .map(type -> new Type(type.getSlot(), type.getName(), type.getUrl()))
                        .collect(Collectors.toList())
        );

        // Mapear habilidades
        dto.setAbilities(
                pokemon.getAbilities().stream()
                        .map(ability -> new Ability(
                                ability.getName(),
                                ability.getUrl(),
                                ability.isHidden(),
                                ability.getSlot()
                        ))
                        .collect(Collectors.toList())
        );

        return dto;
    }
}

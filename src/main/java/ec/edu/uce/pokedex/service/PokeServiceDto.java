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

/**
 * Clase de servicio para gestionar las operaciones relacionadas con Pokémon.
 * Proporciona métodos para obtener y mapear datos de Pokémon.
 */
@Service
@RequiredArgsConstructor
public class PokeServiceDto {

    private final PokemonRepository pokemonRepository;

    /**
     * Mapea una entidad de Pokémon a un DTO de Pokémon.
     * Este método convierte el objeto interno de la entidad `Pokemon` en un `PokemonDto`
     * que puede ser utilizado para la presentación de datos o respuestas de API externas.
     *
     * @param pokemon La entidad de Pokémon a mapear.
     * @return Un objeto `PokemonDto` con los datos de la entidad `Pokemon`.
     */
    private PokemonDto mapToPokemonDto(Pokemon pokemon) {
        return PokemonDto.builder()
                .name(pokemon.getName()) // Extrae el nombre del Pokémon
                .spriteUrl(pokemon.getSprites().getFrontDefault()) // Extrae la URL del sprite del Pokémon
                .types(pokemon.getTypes().stream() // Mapea cada tipo del Pokémon
                        .map(type -> new Type(type.getSlot(), type.getName(), type.getUrl()))
                        .collect(Collectors.toList())) // Colecciona los tipos en una lista
                .abilities(pokemon.getAbilities().stream() // Mapea cada habilidad del Pokémon
                        .map(ability -> new Ability(ability.getName(), ability.getUrl(), ability.isHidden(), ability.getSlot()))
                        .collect(Collectors.toList())) // Colecciona las habilidades en una lista
                .build(); // Construye el DTO del Pokémon
    }

    /**
     * Obtiene una lista paginada de Pokémon y los mapea a DTOs.
     * Este método recupera una lista de Pokémon desde el repositorio y los devuelve
     * como una lista de objetos `PokemonDto`.
     *
     * El método utiliza cache para evitar obtener los mismos datos repetidamente.
     *
     * @param offset El número de página (empezando desde 0).
     * @param limit  El número de elementos por página.
     * @return Una lista de objetos `PokemonDto`.
     */
    @Cacheable("pokemons") // Cachea el resultado para optimizar el rendimiento en solicitudes subsecuentes
    public List<PokemonDto> getPokemonPage(int offset, int limit) {
        // Obtiene una página de Pokémon desde el repositorio, mapeada a DTOs
        Page<PokemonDto> pokemonPage = pokemonRepository.findAll(PageRequest.of(offset, limit))
                .map(this::mapToPokemonDto); // Mapea las entidades a DTOs
        return pokemonPage.getContent(); // Devuelve el contenido de la página (lista de Pokémon DTOs)
    }
}

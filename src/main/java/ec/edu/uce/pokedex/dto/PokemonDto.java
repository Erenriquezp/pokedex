package ec.edu.uce.pokedex.dto;

import ec.edu.uce.pokedex.models.Ability;
import ec.edu.uce.pokedex.models.Type;
import lombok.Builder;
import lombok.Data;

import java.net.ProtocolFamily;
import java.util.List;

@Data
@Builder
public class PokemonDto {
    private String name;
    private List<Type> types;
    private List<Ability> abilities;
    private String spriteUrl;

}

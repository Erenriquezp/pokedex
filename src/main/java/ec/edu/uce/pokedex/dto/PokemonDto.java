package ec.edu.uce.pokedex.dto;

import lombok.Data;

@Data
public class PokemonDto {
    private String name;
    private String type;
    private String ability;
    private String spriteUrl;
}

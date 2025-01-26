package ec.edu.uce.pokedex.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Sprites {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Clave primaria auto-generada
    private Long id;

    private String frontDefault;
    private String backDefault;
    private String frontShiny;
    private String backShiny;
    private String frontFemale;
    private String backFemale;
    private String frontShinyFemale;
    private String backShinyFemale;

    @OneToOne(mappedBy = "sprites") // Relación inversa con Pokémon
    private Pokemon pokemon;
}

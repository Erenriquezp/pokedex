package ec.edu.uce.pokedex.models;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Move {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Clave primaria auto-generada
    private Long id;

    private String name;
    private String url;

    @ManyToOne(fetch = FetchType.EAGER) // Muchos movimientos pertenecen a un Pokémon
    @JoinColumn(name = "pokemon_id") // Clave foránea en Move
    private Pokemon pokemon;

    public Move(String name, String url) {
        this.name = name;
        this.url = url;
    }
}

package ec.edu.uce.pokedex.models;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Ability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremental para la tabla
    private Long id;

    private String name;
    private String url;
    private boolean isHidden;
    private int slot;

    @ManyToOne(fetch = FetchType.LAZY) // Muchas habilidades pueden pertenecer a un Pokémon
    @JoinColumn(name = "pokemon_id") // Clave foránea en Ability
    private Pokemon pokemon;

    public Ability(String name, String url, boolean isHidden, int slot) {
        this.name = name;
        this.url = url;
        this.isHidden = isHidden;
        this.slot = slot;
    }

}

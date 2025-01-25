package ec.edu.uce.pokedex.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Stat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Clave primaria auto-generada
    private Long id;

    private int baseStat;
    private int effort;
    private String name;

    @ManyToOne(fetch = FetchType.EAGER) // Muchas estadísticas pertenecen a un Pokémon
    @JoinColumn(name = "pokemon_id") // Clave foránea en Stat
    @JsonIgnore
    private Pokemon pokemon;

    public Stat(int baseStat, int effort, String name) {
        this.baseStat = baseStat;
        this.effort = effort;
        this.name = name;
    }
}

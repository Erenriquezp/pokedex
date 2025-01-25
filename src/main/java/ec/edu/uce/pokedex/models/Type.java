package ec.edu.uce.pokedex.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Clave primaria auto-generada
    private Long id;

    private int slot;
    private String name;
    private String url;

    @ManyToOne(fetch = FetchType.LAZY) // Muchos tipos pertenecen a un Pokémon
    @JoinColumn(name = "pokemon_id") // Clave foránea en Type
    private Pokemon pokemon;

    public Type(int slot, String name, String url) {
        this.slot = slot;
        this.name = name;
        this.url = url;
    }
}

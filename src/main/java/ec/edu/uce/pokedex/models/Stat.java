package ec.edu.uce.pokedex.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Stat {
    private int baseStat;
    private int effort;
    private String name;
}

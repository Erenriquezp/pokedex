package ec.edu.uce.pokedex.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Pokemon {
    private String name;
    private int id;
    private int weight;
    private int height;
    private Sprites sprites;

    @Data
    public static class Sprites {
        private String frontDefault;
    }
}

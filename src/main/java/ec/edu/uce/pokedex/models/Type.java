package ec.edu.uce.pokedex.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Type {
    private int slot;
    private String name;
    private String url;
}

package ec.edu.uce.pokedex.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ability {
    private String name;
    private String url;
    private boolean isHidden;
    private int slot;
}

package ec.edu.uce.pokedex.models;

import lombok.Data;

import java.util.List;

@Data
public class Pokemon {
    private int id;
    private String name;
    private int baseExperience;
    private int height;
    private int weight;
    private int order;
    private List<Ability> abilities;
    private List<Stat> stats;
    private List<Type> types;
    private List<Move> moves;
    private Species species;
    private Sprites sprites;
}

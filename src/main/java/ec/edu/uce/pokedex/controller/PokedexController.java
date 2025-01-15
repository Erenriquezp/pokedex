package ec.edu.uce.pokedex.controller;

import ec.edu.uce.pokedex.service.PokeService;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import java.awt.*;

@Controller
public class PokedexController {
    private final PokeService pokeService;

    public PokedexController(PokeService pokeService) {
        this.pokeService = pokeService;
    }

    public void startGUI() {
        JFrame frame = new JFrame("Pokedex");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JLabel resultLabel = new JLabel("Results will appear here");

        searchButton.addActionListener(e -> {
            String name = searchField.getText();
            pokeService.getPokemonByName(name)
                    .doOnNext(pokemon -> resultLabel.setText("Found: " + pokemon.getName()))
                    .doOnError(err -> resultLabel.setText("Error: " + err.getMessage()))
                    .subscribe();
        });

        JPanel panel = new JPanel();
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(resultLabel);

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}

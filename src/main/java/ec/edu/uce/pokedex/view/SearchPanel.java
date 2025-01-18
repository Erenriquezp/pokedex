package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.models.Pokemon;
import ec.edu.uce.pokedex.service.PokeService;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class SearchPanel {

    @Getter
    private final JPanel panel;
    private final JLabel resultLabel;

    public SearchPanel(PokeService pokeService) {
        this.panel = new JPanel();
        this.panel.setLayout(new GridLayout(3, 1, 10, 10));

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 18));
        searchField.setHorizontalAlignment(JTextField.CENTER);

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 16));

        this.resultLabel = new JLabel("Enter a Pokémon name to search", SwingConstants.CENTER);
        this.resultLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        // Configurar acción del botón de búsqueda
        searchButton.addActionListener(e -> {
            String name = searchField.getText().trim();
            if (name.isEmpty()) {
                resultLabel.setText("Please enter a valid Pokémon name.");
                return;
            }

            fetchAndDisplayPokemonInfo(pokeService, name);
        });

        panel.add(searchField);
        panel.add(searchButton);
        panel.add(resultLabel);
    }

    private void fetchAndDisplayPokemonInfo(PokeService pokeService, String name) {
        pokeService.getPokemonByName(name.toLowerCase())
                .doOnNext(this::displayPokemonInfo)
                .doOnError(err -> resultLabel.setText("Error: Pokémon not found!"))
                .subscribe();
    }

    private void displayPokemonInfo(Pokemon pokemon) {
        String info = String.format(
                "<html>Pokemon Found!<br>" +
                        "ID: %d<br>" +
                        "Name: %s<br>" +
                        "Base Experience: %d<br>" +
                        "Height: %d<br>" +
                        "Weight: %d<br>" +
                        "Order: %d</html>",
                pokemon.getId(),
                pokemon.getName(),
                pokemon.getBaseExperience(),
                pokemon.getHeight(),
                pokemon.getWeight(),
                pokemon.getOrder()
        );
        resultLabel.setText(info);
    }
}

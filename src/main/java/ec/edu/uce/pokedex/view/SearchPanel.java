package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.models.Pokemon;
import ec.edu.uce.pokedex.service.PokeService;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

@Component
public class SearchPanel {

    @Getter
    private final JPanel panel;
    private final JLabel imageLabel;
    private final JLabel infoLabel;

    public SearchPanel(PokeService pokeService) {
        this.panel = new JPanel(new BorderLayout(10, 10));

        // Título del panel
        JLabel titleLabel = new JLabel("Search Pokémon", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Campo de búsqueda
        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 18));
        searchField.setHorizontalAlignment(JTextField.CENTER);

        // Botón de búsqueda
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 16));

        // Panel superior para la barra de búsqueda
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // Etiqueta para mostrar la imagen del Pokémon
        imageLabel = new JLabel("", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(300, 300));

        // Etiqueta para mostrar la información del Pokémon
        infoLabel = new JLabel("Enter a Pokémon name to search", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        // Panel central para mostrar la imagen y la información
        JPanel displayPanel = new JPanel(new BorderLayout(10, 10));
        displayPanel.add(imageLabel, BorderLayout.CENTER);
        displayPanel.add(infoLabel, BorderLayout.SOUTH);

        // Agregar los componentes al panel principal
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(displayPanel, BorderLayout.SOUTH);

        // Acción del botón de búsqueda
        searchButton.addActionListener(e -> {
            String name = searchField.getText().trim();
            if (name.isEmpty()) {
                infoLabel.setText("Please enter a valid Pokémon name.");
                imageLabel.setIcon(null);
                return;
            }
            fetchAndDisplayPokemonInfo(pokeService, name);
        });
    }

    /**
     * Fetches Pokémon data and updates the display.
     *
     * @param pokeService PokeService instance for fetching Pokémon data.
     * @param name        Pokémon name to search.
     */
    private void fetchAndDisplayPokemonInfo(PokeService pokeService, String name) {
        pokeService.getPokemonByName(name.toLowerCase())
                .doOnNext(this::displayPokemonInfo)
                .doOnError(err -> SwingUtilities.invokeLater(() -> {
                    infoLabel.setText("Error: Pokémon not found!");
                    imageLabel.setIcon(null);
                }))
                .subscribe();
    }

    /**
     * Updates the UI with Pokémon information and sprite.
     *
     * @param pokemon Pokémon data to display.
     */
    private void displayPokemonInfo(Pokemon pokemon) {
        SwingUtilities.invokeLater(() -> {
            // Formatear la información
            String info = String.format(
                    "<html><h2>Pokemon Found!</h2>" +
                            "<b>ID:</b> %d<br>" +
                            "<b>Name:</b> %s<br>" +
                            "<b>Base Experience:</b> %d<br>" +
                            "<b>Height:</b> %d<br>" +
                            "<b>Weight:</b> %d<br>" +
                            "<b>Order:</b> %d</html>",
                    pokemon.getId(),
                    pokemon.getName(),
                    pokemon.getBaseExperience(),
                    pokemon.getHeight(),
                    pokemon.getWeight(),
                    pokemon.getOrder()
            );
            infoLabel.setText(info);

            // Cargar la imagen
            try {
                URL spriteUrl = new URL(pokemon.getSprites().getFrontDefault());
                ImageIcon spriteIcon = new ImageIcon(spriteUrl);
                Image scaledImage = spriteIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            } catch (Exception e) {
                imageLabel.setText("Image not available");
                imageLabel.setIcon(null);
            }
        });
    }
}

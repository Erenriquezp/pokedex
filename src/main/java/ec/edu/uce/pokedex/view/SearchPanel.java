package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.config.UIConfig;
import ec.edu.uce.pokedex.models.Pokemon;
import ec.edu.uce.pokedex.service.PokeService;
import ec.edu.uce.pokedex.util.ComponentFactory;
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
    private final UIConfig uiConfig;

    public SearchPanel(PokeService pokeService, UIConfig uiConfig) {
        this.uiConfig = uiConfig;
        this.panel = new JPanel(new BorderLayout(10, 10));

        // Crear y agregar los componentes principales
        JLabel titleLabel = ComponentFactory.createLabel("Search Pokémon", 24, SwingConstants.CENTER);
        JTextField searchField = createSearchField();
        JButton searchButton = ComponentFactory.createButton("Search", 16, uiConfig.primaryColor(), uiConfig.secondaryColor());
        JPanel searchPanel = ComponentFactory.createSearchPanel(searchField, searchButton);

        this.imageLabel = ComponentFactory.createLabel("", 0, SwingConstants.CENTER);
        this.imageLabel.setPreferredSize(new Dimension(300, 300));

        this.infoLabel = ComponentFactory.createLabel("Enter a Pokémon name to search", 14, SwingConstants.CENTER);
        this.infoLabel.setFont(uiConfig.labelFont());

        JPanel displayPanel = createDisplayPanel();

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(displayPanel, BorderLayout.SOUTH);

        // Acción del botón de búsqueda
        searchButton.addActionListener(e -> {
            String name = searchField.getText().trim();
            if (name.isEmpty()) {
                showErrorMessage("Please enter a valid Pokémon name.");
                return;
            }
            fetchAndDisplayPokemonInfo(pokeService, name);
        });
    }

    /**
     * Crea el campo de texto de búsqueda.
     */
    private JTextField createSearchField() {
        JTextField searchField = new JTextField();
        searchField.setFont(uiConfig.labelFont());
        searchField.setHorizontalAlignment(JTextField.CENTER);
        return searchField;
    }

    /**
     * Crea el panel que muestra la imagen y la información.
     */
    private JPanel createDisplayPanel() {
        JPanel displayPanel = new JPanel(new BorderLayout(10, 10));
        displayPanel.add(imageLabel, BorderLayout.CENTER);
        displayPanel.add(infoLabel, BorderLayout.SOUTH);
        return displayPanel;
    }

    /**
     * Muestra un mensaje de error al usuario.
     *
     * @param message Mensaje de error.
     */
    private void showErrorMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            infoLabel.setText(message);
            imageLabel.setIcon(null);
        });
    }

    /**
     * Obtiene los datos del Pokémon y actualiza la UI.
     *
     * @param pokeService Servicio para obtener datos del Pokémon.
     * @param name        Nombre del Pokémon.
     */
    private void fetchAndDisplayPokemonInfo(PokeService pokeService, String name) {
        pokeService.getPokemonByName(name.toLowerCase())
                .doOnNext(this::displayPokemonInfo)
                .doOnError(err -> showErrorMessage("Error: Pokémon not found!"))
                .subscribe();
    }

    /**
     * Actualiza la interfaz con la información y sprite del Pokémon.
     *
     * @param pokemon Datos del Pokémon.
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

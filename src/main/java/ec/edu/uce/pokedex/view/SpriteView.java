package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.config.UIConfig;
import ec.edu.uce.pokedex.controller.SpriteController;
import ec.edu.uce.pokedex.models.Sprites;
import ec.edu.uce.pokedex.util.ComponentFactory;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class SpriteView {

    @Getter
    private final JPanel panel;
    private final SpriteController controller;
    private final UIConfig uiConfig;

    public SpriteView(SpriteController controller, UIConfig uiConfig) {
        this.controller = controller;
        this.uiConfig = uiConfig;
        this.panel = new JPanel(new BorderLayout());
        initialize();
    }

    private void initialize() {
        JLabel titleLabel = ComponentFactory.createLabel("Search Pokémon Sprites", 18, SwingConstants.CENTER);

        JTextField searchField;
        searchField = ComponentFactory.createTextField(20, JTextField.CENTER);
        JButton searchButton = ComponentFactory.createButton("Search", 16, uiConfig.primaryColor(), uiConfig.secondaryColor());

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.add(searchField);
        inputPanel.add(searchButton);

        JPanel spritePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Flexible layout
        JScrollPane scrollPane = ComponentFactory.createScrollPane(spritePanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        spritePanel.setBorder(BorderFactory.createLineBorder(uiConfig.primaryColor(), 2));
        spritePanel.setBackground(uiConfig.secondaryColor());

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> {
            String pokemonName = searchField.getText().trim();
            if (pokemonName.isEmpty()) {
                showError("Please enter a Pokémon name.");
                return;
            }
            fetchAndDisplaySprites(pokemonName, spritePanel);
        });
    }

    private void fetchAndDisplaySprites(String pokemonName, JPanel spritePanel) {
        SwingWorker<List<ImageIcon>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ImageIcon> doInBackground() {
                try {
                    Sprites sprites = controller.getSpritesForPokemon(pokemonName);
                    return loadSprites(sprites);
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> showError("Error: Unable to fetch sprites."));
                    return new ArrayList<>();
                }
            }

            @Override
            protected void done() {
                try {
                    List<ImageIcon> spriteIcons = get();
                    updateSpritePanel(spriteIcons, spritePanel);
                } catch (Exception e) {
                    showError("Error displaying sprites.");
                }
            }
        };
        worker.execute();
    }

    private List<ImageIcon> loadSprites(Sprites sprites) {
        List<ImageIcon> spriteIcons = new ArrayList<>();
        addSpriteIfValid(spriteIcons, sprites.getFrontDefault());
        addSpriteIfValid(spriteIcons, sprites.getBackDefault());
        addSpriteIfValid(spriteIcons, sprites.getFrontShiny());
        addSpriteIfValid(spriteIcons, sprites.getBackShiny());
        addSpriteIfValid(spriteIcons, sprites.getFrontFemale());
        addSpriteIfValid(spriteIcons, sprites.getBackFemale());
        addSpriteIfValid(spriteIcons, sprites.getFrontShinyFemale());
        addSpriteIfValid(spriteIcons, sprites.getBackShinyFemale());
        return spriteIcons;
    }

    private void addSpriteIfValid(List<ImageIcon> spriteIcons, String spriteUrl) {
        if (spriteUrl != null && !spriteUrl.isEmpty()) {
            try {
                URL url = new URL(spriteUrl);
                ImageIcon spriteIcon = new ImageIcon(url);
                spriteIcons.add(new ImageIcon(spriteIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH))); // Escalar imagen
            } catch (Exception ignored) {
                // Ignorar errores de carga de imagen
            }
        }
    }

    private void updateSpritePanel(List<ImageIcon> spriteIcons, JPanel spritePanel) {
        SwingUtilities.invokeLater(() -> {
            spritePanel.removeAll();

            // Nombres de los sprites
            String[] spriteNames = {
                    "Front Default", "Back Default", "Front Shiny", "Back Shiny",
                    "Front Female", "Back Female", "Front Shiny Female", "Back Shiny Female"
            };

            // Crear un GridLayout para mostrar en tres filas
            int rows = (int) Math.ceil(spriteIcons.size() / 3.0); // Calcular el número de filas
            spritePanel.setLayout(new GridLayout(rows, 3, 10, 10)); // 3 columnas, espacio de 10 entre componentes

            for (int i = 0; i < spriteIcons.size(); i++) {
                ImageIcon spriteIcon = spriteIcons.get(i);
                JLabel spriteLabel = new JLabel(spriteIcon);
                spriteLabel.setHorizontalAlignment(SwingConstants.CENTER);

                // Crear un panel para contener la imagen y el nombre
                JPanel spriteContainer = new JPanel(new BorderLayout());
                spriteContainer.add(spriteLabel, BorderLayout.CENTER);
                JLabel nameLabel = ComponentFactory.createLabel(spriteNames[i].toUpperCase(), 14, SwingConstants.CENTER);
                spriteContainer.add(nameLabel, BorderLayout.SOUTH); // Añadir el nombre del sprite

                spritePanel.add(spriteContainer);
            }

            spritePanel.revalidate();
            spritePanel.repaint();
        });
    }

    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}

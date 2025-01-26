package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.config.UIConfig;
import ec.edu.uce.pokedex.controller.SpriteController;
import ec.edu.uce.pokedex.exception.SpriteFetchException;
import ec.edu.uce.pokedex.models.Sprites;
import ec.edu.uce.pokedex.util.ComponentFactory;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
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

        JTextField searchField = ComponentFactory.createTextField(20, JTextField.CENTER);
        JButton searchButton = ComponentFactory.createButton("Search", 16, uiConfig.primaryColor(), uiConfig.secondaryColor());

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.add(searchField);
        inputPanel.add(searchButton);

        JPanel spritePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
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
                    throw new SpriteFetchException("Failed to fetch sprites for Pokémon: " + pokemonName, e);
                }
            }

            @Override
            protected void done() {
                try {
                    List<ImageIcon> spriteIcons = get();
                    updateSpritePanel(spriteIcons, spritePanel);
                } catch (SpriteFetchException e) {
                    showError(e.getMessage());
                } catch (Exception e) {
                    showError("Unexpected error occurred while fetching sprites.");
                }
            }
        };
        worker.execute();
    }

    private List<ImageIcon> loadSprites(Sprites sprites) {
        List<ImageIcon> spriteIcons = new ArrayList<>();
        try {
            addSpriteIfValid(spriteIcons, sprites.getFrontDefault());
            addSpriteIfValid(spriteIcons, sprites.getBackDefault());
            addSpriteIfValid(spriteIcons, sprites.getFrontShiny());
            addSpriteIfValid(spriteIcons, sprites.getBackShiny());
            addSpriteIfValid(spriteIcons, sprites.getFrontFemale());
            addSpriteIfValid(spriteIcons, sprites.getBackFemale());
            addSpriteIfValid(spriteIcons, sprites.getFrontShinyFemale());
            addSpriteIfValid(spriteIcons, sprites.getBackShinyFemale());
        } catch (Exception e) {
            throw new SpriteFetchException("Error while loading sprites.", e);
        }
        return spriteIcons;
    }

    private void addSpriteIfValid(List<ImageIcon> spriteIcons, String spriteUrl) {
        if (spriteUrl != null && !spriteUrl.isEmpty()) {
            try {
                URI uri = new URI(spriteUrl);
                ImageIcon spriteIcon = new ImageIcon(uri.toURL());
                spriteIcons.add(new ImageIcon(spriteIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
            } catch (Exception ignored) {
                // Log the error if necessary, but avoid stopping the flow.
            }
        }
    }

    private void updateSpritePanel(List<ImageIcon> spriteIcons, JPanel spritePanel) {
        SwingUtilities.invokeLater(() -> {
            spritePanel.removeAll();

            String[] spriteNames = {
                    "Front Default", "Back Default", "Front Shiny", "Back Shiny",
                    "Front Female", "Back Female", "Front Shiny Female", "Back Shiny Female"
            };

            int rows = (int) Math.ceil(spriteIcons.size() / 3.0);
            spritePanel.setLayout(new GridLayout(rows, 3, 10, 10));

            for (int i = 0; i < spriteIcons.size(); i++) {
                ImageIcon spriteIcon = spriteIcons.get(i);
                JLabel spriteLabel = new JLabel(spriteIcon);
                spriteLabel.setHorizontalAlignment(SwingConstants.CENTER);

                JPanel spriteContainer = new JPanel(new BorderLayout());
                spriteContainer.add(spriteLabel, BorderLayout.CENTER);
                JLabel nameLabel = ComponentFactory.createLabel(spriteNames[i].toUpperCase(), 14, SwingConstants.CENTER);
                spriteContainer.add(nameLabel, BorderLayout.SOUTH);

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
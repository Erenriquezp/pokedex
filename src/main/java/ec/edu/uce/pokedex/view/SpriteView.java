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

/**
 * The SpriteView class is responsible for displaying the user interface for searching and displaying Pokémon sprites.
 * It allows users to search for a Pokémon by name and see various sprite images (front, back, shiny, etc.).
 */
@Component
public class SpriteView {

    @Getter
    private final JPanel panel;
    private final SpriteController controller;
    private final UIConfig uiConfig;

    /**
     * Constructor for SpriteView.
     * Initializes the UI components and sets up the event listeners.
     *
     * @param controller The controller responsible for fetching sprite data.
     * @param uiConfig   The configuration for UI styling.
     */
    public SpriteView(SpriteController controller, UIConfig uiConfig) {
        this.controller = controller;
        this.uiConfig = uiConfig;
        this.panel = new JPanel(new BorderLayout(10, 10));
        initialize();
    }

    /**
     * Initializes the UI components including the title, search bar, and sprite panel.
     * Sets up the layout and event listeners for the search button.
     */
    private void initialize() {
        // Create title with style and margins
        JLabel titleLabel = ComponentFactory.createLabel("Search Pokémon Sprites", 35, SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Create search bar
        JTextField searchField = createSearchField();
        JButton searchButton = createSearchButton();

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.setBackground(uiConfig.secondaryColor());

        // Container for title and search bar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(searchPanel, BorderLayout.SOUTH);

        JPanel spritePanel = new JPanel();
        JScrollPane scrollPane = ComponentFactory.createScrollPane(spritePanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        spritePanel.setBackground(uiConfig.secondaryColor());

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add action listener for search button
        searchButton.addActionListener(e -> {
            String pokemonName = searchField.getText().trim();
            if (pokemonName.isEmpty()) {
                showError("Please enter a Pokémon name.");
                return;
            }
            fetchAndDisplaySprites(pokemonName, spritePanel);
        });
    }

    /**
     * Creates and returns a JTextField for the search bar.
     *
     * @return The JTextField component for entering Pokémon names.
     */
    private JTextField createSearchField() {
        JTextField searchField = new JTextField(20);
        searchField.setFont(uiConfig.labelFont());
        searchField.setHorizontalAlignment(JTextField.CENTER);
        searchField.setBorder(BorderFactory.createLineBorder(uiConfig.primaryColor(), 2));
        return searchField;
    }

    /**
     * Creates and returns a JButton for the search functionality.
     *
     * @return The JButton component for initiating a Pokémon sprite search.
     */
    private JButton createSearchButton() {
        return ComponentFactory.createButton("Search", 16, uiConfig.primaryColor(), uiConfig.secondaryColor());
    }

    /**
     * Fetches the Pokémon sprites asynchronously based on the provided name and displays them on the sprite panel.
     *
     * @param pokemonName The name of the Pokémon to search for.
     * @param spritePanel The panel where the sprites will be displayed.
     */
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

    /**
     * Loads and returns a list of ImageIcons based on the provided sprite URLs.
     *
     * @param sprites The Sprites object containing URLs for various sprite types.
     * @return A list of ImageIcons for the valid sprites.
     */
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

    /**
     * Adds a sprite to the list of ImageIcons if the sprite URL is valid.
     *
     * @param spriteIcons The list of ImageIcons to add the sprite to.
     * @param spriteUrl   The URL of the sprite image to load.
     */
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

    /**
     * Updates the sprite panel with the given sprite icons.
     * It arranges the icons in a grid layout with labels for each sprite type.
     *
     * @param spriteIcons The list of ImageIcons to display.
     * @param spritePanel The panel to update with the sprite icons.
     */
    private void updateSpritePanel(List<ImageIcon> spriteIcons, JPanel spritePanel) {
        SwingUtilities.invokeLater(() -> {
            spritePanel.removeAll();

            String[] spriteNames = {
                    "Front Default", "Back Default", "Front Shiny", "Back Shiny",
                    "Front Female", "Back Female", "Front Shiny Female", "Back Shiny Female"
            };

            spritePanel.setLayout(new GridLayout(0, 3, 10, 10));

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

    /**
     * Displays an error message in a pop-up dialog.
     *
     * @param message The error message to display.
     */
    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}

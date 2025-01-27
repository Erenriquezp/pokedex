package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.config.UIConfig;
import ec.edu.uce.pokedex.controller.EvolutionController;
import ec.edu.uce.pokedex.exception.EvolutionFetchException;
import ec.edu.uce.pokedex.util.ComponentFactory;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * The EvolutionView class is responsible for managing the user interface related to displaying a Pokémon's evolution chain.
 * It allows users to search for a Pokémon species by name and view its evolutionary stages.
 */
@Component
public class EvolutionView {

    @Getter
    private final JPanel panel;
    private final EvolutionController controller;
    private final UIConfig uiConfig;

    /**
     * Constructor for the EvolutionView class.
     * Initializes the view components and sets up the event listeners.
     *
     * @param controller The controller responsible for fetching evolution chain data.
     * @param uiConfig   The configuration for UI styling.
     */
    public EvolutionView(EvolutionController controller, UIConfig uiConfig) {
        this.controller = controller;
        this.uiConfig = uiConfig;
        this.panel = new JPanel(new BorderLayout());
        initialize();
    }

    /**
     * Initializes the components of the EvolutionView.
     * Sets up the title, search bar, and evolution display area.
     */
    private void initialize() {
        // Create title label
        JLabel titleLabel = ComponentFactory.createLabel("Pokémon Evolution Chain Viewer", 28, SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Create search bar and button
        JTextField searchField = createSearchField();
        JButton searchButton = ComponentFactory.createButton("Search", 18, uiConfig.primaryColor(), uiConfig.secondaryColor());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.setBackground(uiConfig.secondaryColor());

        // Header panel to hold title and search bar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(searchPanel, BorderLayout.SOUTH);

        // Evolution panel to display evolutionary stages
        JPanel evolutionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 30));
        JScrollPane scrollPane = new JScrollPane(evolutionPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Set action listener for search button
        searchButton.addActionListener(e -> {
            String speciesName = searchField.getText().trim();
            if (speciesName.isEmpty()) {
                showErrorMessage("Please enter a species name.");
                return;
            }
            fetchAndDisplayEvolutionChain(speciesName, evolutionPanel);
        });
    }

    /**
     * Creates the search field with styling.
     *
     * @return A JTextField that allows users to input a Pokémon species name.
     */
    private JTextField createSearchField() {
        JTextField searchField = new JTextField(25);
        searchField.setFont(uiConfig.labelFont());
        searchField.setHorizontalAlignment(JTextField.CENTER);
        searchField.setBorder(BorderFactory.createLineBorder(uiConfig.primaryColor(), 2));
        return searchField;
    }

    /**
     * Fetches the evolution chain for the given Pokémon species and displays it in the provided panel.
     * This is done asynchronously to avoid blocking the UI.
     *
     * @param speciesName   The name of the Pokémon species to fetch the evolution chain for.
     * @param evolutionPanel The panel where the evolution chain will be displayed.
     */
    private void fetchAndDisplayEvolutionChain(String speciesName, JPanel evolutionPanel) {
        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Map<String, Object>> doInBackground() {
                try {
                    return controller.getEvolutionChain(speciesName).block();
                } catch (Exception e) {
                    throw new EvolutionFetchException("Failed to fetch evolution chain for: " + speciesName, e);
                }
            }

            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> chain = get();
                    if (chain == null || chain.isEmpty()) {
                        showErrorMessage("No evolution chain found for: " + speciesName);
                    } else {
                        populateEvolutionPanel(chain, evolutionPanel);
                    }
                } catch (EvolutionFetchException e) {
                    showErrorMessage(e.getMessage());
                } catch (Exception e) {
                    showErrorMessage("An unexpected error occurred while displaying the evolution chain.");
                }
            }
        };
        worker.execute();
    }

    /**
     * Populates the evolution panel with the evolution stages.
     *
     * @param chain          The list of evolution stages to display.
     * @param evolutionPanel The panel to populate with evolution details.
     */
    private void populateEvolutionPanel(List<Map<String, Object>> chain, JPanel evolutionPanel) {
        SwingUtilities.invokeLater(() -> {
            evolutionPanel.removeAll();

            // Iterate through each evolution stage and add it to the panel
            chain.forEach(stage -> {
                JPanel stagePanel = createEvolutionStagePanel(stage);
                evolutionPanel.add(stagePanel);
            });

            evolutionPanel.revalidate();
            evolutionPanel.repaint();
        });
    }

    /**
     * Creates a panel for displaying a specific evolution stage.
     *
     * @param stage A map containing the data for the evolution stage.
     * @return A JPanel displaying the evolution stage.
     */
    private JPanel createEvolutionStagePanel(Map<String, Object> stage) {
        JPanel stagePanel = new JPanel(new BorderLayout());
        stagePanel.setBorder(BorderFactory.createLineBorder(uiConfig.tertiaryColor(), 2));
        stagePanel.setBackground(uiConfig.secondaryColor());

        Map<String, Object> species = (Map<String, Object>) stage.get("species");
        String speciesName = (String) species.get("name");
        String imageUrl = String.format(
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/%s.png",
                extractIdFromUrl((String) species.get("url"))
        );

        JLabel nameLabel = ComponentFactory.createLabel(speciesName.toUpperCase(), 14, SwingConstants.CENTER);
        JLabel spriteLabel = createSpriteLabel(imageUrl);

        stagePanel.add(nameLabel, BorderLayout.NORTH);
        stagePanel.add(spriteLabel, BorderLayout.CENTER);
        return stagePanel;
    }

    /**
     * Creates a JLabel for the Pokémon sprite, either displaying the image or an error message if the image cannot be loaded.
     *
     * @param imageUrl The URL of the Pokémon sprite image.
     * @return A JLabel displaying the sprite or an error message.
     */
    private JLabel createSpriteLabel(String imageUrl) {
        JLabel spriteLabel = new JLabel();
        spriteLabel.setHorizontalAlignment(SwingConstants.CENTER);

        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    URI uri = new URI(imageUrl);
                    ImageIcon spriteIcon = new ImageIcon(uri.toURL());
                    return new ImageIcon(spriteIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH));
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    ImageIcon spriteIcon = get();
                    if (spriteIcon != null) {
                        spriteLabel.setIcon(spriteIcon);
                    } else {
                        spriteLabel.setText("Image not available");
                        spriteLabel.setFont(uiConfig.labelFont());
                    }
                } catch (Exception e) {
                    spriteLabel.setText("Error loading image");
                    spriteLabel.setFont(uiConfig.labelFont());
                }
            }
        };
        worker.execute();

        return spriteLabel;
    }

    /**
     * Extracts the Pokémon ID from the species URL.
     *
     * @param url The URL of the species data.
     * @return The extracted Pokémon ID.
     */
    private String extractIdFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }

    /**
     * Displays an error message to the user in a dialog.
     *
     * @param message The error message to display.
     */
    private void showErrorMessage(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}

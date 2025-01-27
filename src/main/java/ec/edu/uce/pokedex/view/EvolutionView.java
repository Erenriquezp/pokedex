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

@Component
public class EvolutionView {

    @Getter
    private final JPanel panel;
    private final EvolutionController controller;
    private final UIConfig uiConfig;

    public EvolutionView(EvolutionController controller, UIConfig uiConfig) {
        this.controller = controller;
        this.uiConfig = uiConfig;
        this.panel = new JPanel(new BorderLayout());
        initialize();
    }

    private void initialize() {
        // Crear título
        JLabel titleLabel = ComponentFactory.createLabel("Pokémon Evolution Chain Viewer", 28, SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Crear barra de búsqueda
        JTextField searchField = createSearchField();
        JButton searchButton = ComponentFactory.createButton("Search", 18, uiConfig.primaryColor(), uiConfig.secondaryColor());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.setBackground(uiConfig.secondaryColor());

        // Contenedor para título y barra de búsqueda
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(searchPanel, BorderLayout.SOUTH);

        // Crear panel para mostrar la evolución
        JPanel evolutionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 30));
        JScrollPane scrollPane = new JScrollPane(evolutionPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Agregar componentes principales al panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Acciones del botón de búsqueda
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
     * @return JTextField styled search field.
     */
    private JTextField createSearchField() {
        JTextField searchField = new JTextField(25);
        searchField.setFont(uiConfig.labelFont());
        searchField.setHorizontalAlignment(JTextField.CENTER);
        searchField.setBorder(BorderFactory.createLineBorder(uiConfig.primaryColor(), 2));
        return searchField;
    }

    /**
     * Fetches and displays the evolution chain for a given Pokémon species.
     *
     * @param speciesName   Name of the Pokémon species.
     * @param evolutionPanel Panel to display the evolution chain.
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
     * Populates the evolution panel with data.
     *
     * @param chain          List of evolution stages.
     * @param evolutionPanel Panel to populate with evolution details.
     */
    private void populateEvolutionPanel(List<Map<String, Object>> chain, JPanel evolutionPanel) {
        SwingUtilities.invokeLater(() -> {
            evolutionPanel.removeAll();

            chain.forEach(stage -> {
                JPanel stagePanel = createEvolutionStagePanel(stage);
                evolutionPanel.add(stagePanel);
            });

            evolutionPanel.revalidate();
            evolutionPanel.repaint();
        });
    }

    /**
     * Creates a panel for an evolution stage.
     *
     * @param stage Map containing the evolution stage data.
     * @return JPanel representing the evolution stage.
     */
    private JPanel createEvolutionStagePanel(Map<String, Object> stage) {
        JPanel stagePanel = new JPanel(new BorderLayout());
        stagePanel.setBorder(BorderFactory.createLineBorder(uiConfig.tertiaryColor(), 3));
        stagePanel.setBackground(uiConfig.secondaryColor());
        stagePanel.setPreferredSize(new Dimension(220, 270));

        Map<String, Object> species = (Map<String, Object>) stage.get("species");
        String speciesName = (String) species.get("name");
        String imageUrl = String.format(
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/%s.png",
                extractIdFromUrl((String) species.get("url"))
        );

        JLabel nameLabel = ComponentFactory.createLabel(speciesName.toUpperCase(), 16, SwingConstants.CENTER);
        JLabel spriteLabel = createSpriteLabel(imageUrl);

        stagePanel.add(nameLabel, BorderLayout.NORTH);
        stagePanel.add(spriteLabel, BorderLayout.CENTER);
        return stagePanel;
    }

    /**
     * Creates a JLabel for the Pokémon sprite.
     *
     * @param imageUrl URL of the Pokémon sprite.
     * @return JLabel with the sprite or an error message.
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
                    return new ImageIcon(spriteIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH));
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
     * @param url URL of the species.
     * @return Extracted Pokémon ID.
     */
    private String extractIdFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }

    /**
     * Displays an error message to the user.
     *
     * @param message Error message to display.
     */
    private void showErrorMessage(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}

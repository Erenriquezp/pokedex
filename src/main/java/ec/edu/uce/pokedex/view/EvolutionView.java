package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.config.UIConfig;
import ec.edu.uce.pokedex.controller.EvolutionController;
import ec.edu.uce.pokedex.util.ComponentFactory;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
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

    /**
     * Initializes the components of the EvolutionView.
     */
    private void initialize() {
        JLabel titleLabel = ComponentFactory.createLabel("Pokémon Evolution Chain Viewer", 26, SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField searchField = createSearchField();
        JButton searchButton = ComponentFactory.createButton("Search", 16, uiConfig.primaryColor(), uiConfig.secondaryColor());

        // Cambiar el layout del panel de búsqueda a FlowLayout
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.setBackground(uiConfig.secondaryColor());

        JPanel evolutionPanel = new JPanel();
        evolutionPanel.setLayout(new BoxLayout(evolutionPanel, BoxLayout.X_AXIS));
        evolutionPanel.setBackground(uiConfig.secondaryColor());

        JScrollPane scrollPane = new JScrollPane(evolutionPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

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
        JTextField searchField = new JTextField(20);
        searchField.setFont(uiConfig.labelFont());
        searchField.setHorizontalAlignment(JTextField.CENTER);
        return searchField;
    }

    /**
     * Fetches and displays the evolution chain for a given Pokémon species.
     *
     * @param speciesName   Name of the Pokémon species.
     * @param evolutionPanel Panel to display the evolution chain.
     */
    private void fetchAndDisplayEvolutionChain(String speciesName, JPanel evolutionPanel) {
        controller.getEvolutionChain(speciesName)
                .doOnNext(chain -> SwingUtilities.invokeLater(() -> populateEvolutionPanel(chain, evolutionPanel)))
                .doOnError(err -> showErrorMessage("Error: Unable to fetch evolution chain."))
                .subscribe();
    }

    /**
     * Populates the evolution panel with data.
     *
     * @param chain          List of evolution stages.
     * @param evolutionPanel Panel to populate with evolution details.
     */
    private void populateEvolutionPanel(List<Map<String, Object>> chain, JPanel evolutionPanel) {
        evolutionPanel.removeAll();

        chain.forEach(stage -> {
            JPanel stagePanel = createEvolutionStagePanel(stage);
            evolutionPanel.add(stagePanel);
        });

        evolutionPanel.revalidate();
        evolutionPanel.repaint();
    }

    /**
     * Creates a panel for a single evolution stage.
     *
     * @param stage Evolution stage data.
     * @return JPanel representing the evolution stage.
     */
    private JPanel createEvolutionStagePanel(Map<String, Object> stage) {
        JPanel stagePanel = new JPanel(new BorderLayout());
        stagePanel.setBorder(BorderFactory.createLineBorder(uiConfig.primaryColor(), 2));
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
     * Creates a JLabel for the Pokémon sprite.
     *
     * @param imageUrl URL of the Pokémon sprite.
     * @return JLabel with the sprite or an error message.
     */
    private JLabel createSpriteLabel(String imageUrl) {
        JLabel spriteLabel = new JLabel();
        spriteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon spriteIcon = new ImageIcon(new URL(imageUrl));
            Image scaledImage = spriteIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            spriteLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            spriteLabel.setText("Image not available");
            spriteLabel.setFont(uiConfig.labelFont());
        }
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
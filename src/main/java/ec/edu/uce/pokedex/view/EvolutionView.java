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

    /**
     * Initializes the components of the EvolutionView.
     */
    private void initialize() {
        JLabel titleLabel = ComponentFactory.createLabel("Pokémon Evolution Chain Viewer", 26, SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField searchField = createSearchField();
        JButton searchButton = ComponentFactory.createButton("Search", 16, uiConfig.primaryColor(), uiConfig.secondaryColor());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.setBackground(uiConfig.secondaryColor());

        JPanel evolutionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        JScrollPane scrollPane = new JScrollPane(evolutionPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> {
            String speciesName = searchField.getText().trim();
            if (speciesName.isEmpty()) {
                showErrorMessage("Please enter a species name.");
                return;
            }
            fetchAndDisplayEvolutionChain(speciesName, evolutionPanel);
        });
    }

    private JTextField createSearchField() {
        JTextField searchField = new JTextField(20);
        searchField.setFont(uiConfig.labelFont());
        searchField.setHorizontalAlignment(JTextField.CENTER);
        return searchField;
    }

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

    private JLabel createSpriteLabel(String imageUrl) {
        JLabel spriteLabel = new JLabel();
        spriteLabel.setHorizontalAlignment(SwingConstants.CENTER);

        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    URI uri = new URI(imageUrl);
                    ImageIcon spriteIcon = new ImageIcon(uri.toURL());
                    return new ImageIcon(spriteIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
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

    private String extractIdFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }

    private void showErrorMessage(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}

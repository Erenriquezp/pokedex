package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.config.UIConfig;
import ec.edu.uce.pokedex.controller.TypeController;
import ec.edu.uce.pokedex.models.Pokemon;
import ec.edu.uce.pokedex.util.ComponentFactory;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.List;

@Component
public class TypeView {

    @Getter
    private final JPanel panel;
    private final TypeController controller;
    private final UIConfig uiConfig;

    public TypeView(TypeController controller, UIConfig uiConfig) {
        this.controller = controller;
        this.uiConfig = uiConfig;
        this.panel = new JPanel(new BorderLayout());
        initialize();
    }

    private void initialize() {
        JLabel titleLabel = ComponentFactory.createLabel("Pokémon by Type Viewer", 24, SwingConstants.CENTER);
        JTextField searchField = ComponentFactory.createTextField(20, JTextField.CENTER);
        JButton searchButton = createSearchButton(searchField);

        JPanel searchPanel = createSearchPanel(searchField, searchButton);
        JScrollPane pokemonPanel = createPokemonPanel();

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(pokemonPanel, BorderLayout.CENTER);
    }

    private JButton createSearchButton(JTextField searchField) {
        JButton searchButton = ComponentFactory.createButton("Search", 16, uiConfig.primaryColor(), uiConfig.secondaryColor());
        searchButton.addActionListener(e -> {
            String typeName = searchField.getText().trim();
            if (typeName.isEmpty()) {
                showError("Please enter a type name.");
            } else {
                fetchAndDisplayPokemonByType(typeName);
            }
        });
        return searchButton;
    }

    private JPanel createSearchPanel(JTextField searchField, JButton searchButton) {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchField.setBorder(BorderFactory.createLineBorder(uiConfig.primaryColor(), 2));
        return searchPanel;
    }

    private JScrollPane createPokemonPanel() {
        JPanel pokemonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        JScrollPane scrollPane = new JScrollPane(pokemonPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return scrollPane;
    }

    private void fetchAndDisplayPokemonByType(String typeName) {
        SwingWorker<List<Pokemon>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Pokemon> doInBackground() {
                try {
                    return controller.getPokemonByType(typeName);
                } catch (Exception e) {
                    showError("Error: Unable to fetch Pokémon by type.");
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    List<Pokemon> pokemons = get();
                    if (pokemons != null) {
                        populatePanel(pokemons);
                    }
                } catch (Exception e) {
                    showError("Error displaying Pokémon.");
                }
            }
        };
        worker.execute();
    }

    private void populatePanel(List<Pokemon> pokemons) {
        JPanel pokemonPanel = (JPanel) ((JScrollPane) panel.getComponent(2)).getViewport().getView();
        SwingUtilities.invokeLater(() -> {
            pokemonPanel.removeAll();
            pokemons.forEach(pokemon -> pokemonPanel.add(createPokemonCard(pokemon)));
            pokemonPanel.revalidate();
            pokemonPanel.repaint();
        });
    }

    private JPanel createPokemonCard(Pokemon pokemon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(uiConfig.tertiaryColor(), 2));
        card.setBackground(uiConfig.secondaryColor());

        JLabel nameLabel = ComponentFactory.createLabel(pokemon.getName().toUpperCase(), 14, SwingConstants.CENTER);
        JLabel spriteLabel = createSpriteLabel(pokemon.getSprites().getFrontDefault());

        card.add(nameLabel, BorderLayout.NORTH);
        card.add(spriteLabel, BorderLayout.CENTER);
        return card;
    }

    private JLabel createSpriteLabel(String spriteUrl) {
        JLabel spriteLabel = new JLabel();
        spriteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loadImage(spriteUrl, spriteLabel);
        return spriteLabel;
    }

    private void loadImage(String spriteUrl, JLabel spriteLabel) {
        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    URI uri = new URI(spriteUrl);
                    return new ImageIcon(new ImageIcon(uri.toURL()).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
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
                    }
                } catch (Exception e) {
                    spriteLabel.setText("Error loading image");
                }
                spriteLabel.setFont(uiConfig.labelFont());
            }
        }.execute();
    }
    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}
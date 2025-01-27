package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.config.UIConfig;
import ec.edu.uce.pokedex.controller.TypeController;
import ec.edu.uce.pokedex.models.Pokemon;
import ec.edu.uce.pokedex.util.ComponentFactory;
import ec.edu.uce.pokedex.util.WrapLayout;
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
        this.panel = new JPanel(new BorderLayout(10, 10));
        initialize();
    }

    private void initialize() {
        // Crear título
        JLabel titleLabel = ComponentFactory.createLabel("Pokémon by Type Viewer", 28, SwingConstants.CENTER);
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

        // Crear panel de resultados
        JScrollPane scrollPane = createPokemonScrollPane();

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Acciones del botón de búsqueda
        searchButton.addActionListener(e -> {
            String typeName = searchField.getText().trim();
            if (typeName.isEmpty()) {
                showError("Please enter a type name.");
            } else {
                fetchAndDisplayPokemonByType(typeName);
            }
        });
    }

    private JTextField createSearchField() {
        JTextField searchField = new JTextField(25);
        searchField.setFont(uiConfig.labelFont());
        searchField.setHorizontalAlignment(JTextField.CENTER);
        searchField.setBorder(BorderFactory.createLineBorder(uiConfig.primaryColor(), 2));
        return searchField;
    }

    private JScrollPane createPokemonScrollPane() {
        JPanel pokemonPanel = new JPanel();
        pokemonPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 15, 15));
        pokemonPanel.setBackground(uiConfig.secondaryColor());

        JScrollPane scrollPane = new JScrollPane(pokemonPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
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
                        populatePokemonPanel(pokemons);
                    } else {
                        showError("No Pokémon found for this type.");
                    }
                } catch (Exception e) {
                    showError("Error displaying Pokémon.");
                }
            }
        };
        worker.execute();
    }

    private void populatePokemonPanel(List<Pokemon> pokemons) {
        JPanel pokemonPanel = (JPanel) ((JScrollPane) panel.getComponent(1)).getViewport().getView();
        SwingUtilities.invokeLater(() -> {
            pokemonPanel.removeAll();
            pokemons.forEach(pokemon -> pokemonPanel.add(createPokemonCard(pokemon)));
            pokemonPanel.revalidate();
            pokemonPanel.repaint();
        });
    }

    private JPanel createPokemonCard(Pokemon pokemon) {
        JPanel card = ComponentFactory.createPanel(new BorderLayout(10, 10), uiConfig.secondaryColor());
        card.setBorder(BorderFactory.createLineBorder(uiConfig.tertiaryColor(), 3));

        JLabel nameLabel = ComponentFactory.createLabel(pokemon.getName().toUpperCase(), 18, SwingConstants.CENTER);
        JLabel spriteLabel = new JLabel();
        spriteLabel.setHorizontalAlignment(SwingConstants.CENTER);

        loadImageAsync(pokemon.getSprites().getFrontDefault(), spriteLabel);

        card.add(nameLabel, BorderLayout.NORTH);
        card.add(spriteLabel, BorderLayout.CENTER);

        return card;
    }

    private void loadImageAsync(String spriteUrl, JLabel spriteLabel) {
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
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
        };
        worker.execute();
    }

    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}

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

/**
 * The TypeView class is responsible for displaying Pokémon by type in the user interface.
 * It allows the user to search for Pokémon by type and view the results in a scrollable panel.
 */
@Component
public class TypeView {

    @Getter
    private final JPanel panel;
    private final TypeController controller;
    private final UIConfig uiConfig;

    /**
     * Constructor for the TypeView class.
     * Initializes the view and sets up the components and event listeners.
     *
     * @param controller The controller responsible for fetching Pokémon data by type.
     * @param uiConfig   The UI configuration for styling components.
     */
    public TypeView(TypeController controller, UIConfig uiConfig) {
        this.controller = controller;
        this.uiConfig = uiConfig;
        this.panel = new JPanel(new BorderLayout(10, 10));
        initialize();
    }

    /**
     * Initializes the UI components of the TypeView, including the title, search bar, and result panel.
     * It also sets the action listener for the search button.
     */
    private void initialize() {
        // Create title label
        JLabel titleLabel = ComponentFactory.createLabel("Pokémon by Type Viewer", 28, SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Create search field and search button
        JTextField searchField = createSearchField();
        JButton searchButton = ComponentFactory.createButton("Search", 18, uiConfig.primaryColor(), uiConfig.secondaryColor());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.setBackground(uiConfig.secondaryColor());

        // Create panel for header (title and search bar)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(searchPanel, BorderLayout.SOUTH);

        // Create scroll pane for displaying Pokémon results
        JScrollPane scrollPane = createPokemonScrollPane();

        // Add header and scroll pane to the main panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Set action listener for search button
        searchButton.addActionListener(e -> {
            String typeName = searchField.getText().trim();
            if (typeName.isEmpty()) {
                showError("Please enter a type name.");
            } else {
                fetchAndDisplayPokemonByType(typeName);
            }
        });
    }

    /**
     * Creates and styles the search input field.
     *
     * @return A styled JTextField for searching Pokémon by type.
     */
    private JTextField createSearchField() {
        JTextField searchField = new JTextField(25);
        searchField.setFont(uiConfig.labelFont());
        searchField.setHorizontalAlignment(JTextField.CENTER);
        searchField.setBorder(BorderFactory.createLineBorder(uiConfig.primaryColor(), 2));
        return searchField;
    }

    /**
     * Creates the scroll pane for displaying Pokémon by type, which includes a layout for cards.
     *
     * @return A JScrollPane containing a panel to display Pokémon.
     */
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

    /**
     * Fetches and displays the list of Pokémon for a given type.
     * This method runs asynchronously to avoid blocking the UI.
     *
     * @param typeName The type of Pokémon to search for (e.g., "Fire", "Water").
     */
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

    /**
     * Populates the Pokémon display panel with Pokémon cards.
     *
     * @param pokemons A list of Pokémon to be displayed in the UI.
     */
    private void populatePokemonPanel(List<Pokemon> pokemons) {
        JPanel pokemonPanel = (JPanel) ((JScrollPane) panel.getComponent(1)).getViewport().getView();
        SwingUtilities.invokeLater(() -> {
            pokemonPanel.removeAll();
            pokemons.forEach(pokemon -> pokemonPanel.add(createPokemonCard(pokemon)));
            pokemonPanel.revalidate();
            pokemonPanel.repaint();
        });
    }

    /**
     * Creates a panel card for a Pokémon displaying its name and sprite.
     *
     * @param pokemon The Pokémon to create the card for.
     * @return A JPanel representing the Pokémon card with its name and sprite.
     */
    private JPanel createPokemonCard(Pokemon pokemon) {
        JPanel card = ComponentFactory.createPanel(new BorderLayout(10, 10), uiConfig.secondaryColor());
        card.setBorder(BorderFactory.createLineBorder(uiConfig.tertiaryColor(), 3));

        JLabel nameLabel = ComponentFactory.createLabel(pokemon.getName().toUpperCase(), 18, SwingConstants.CENTER);
        JLabel spriteLabel = new JLabel();
        spriteLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Load Pokémon sprite asynchronously
        loadImageAsync(pokemon.getSprites().getFrontDefault(), spriteLabel);

        card.add(nameLabel, BorderLayout.NORTH);
        card.add(spriteLabel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Loads an image asynchronously to display the Pokémon sprite.
     *
     * @param spriteUrl The URL of the Pokémon sprite image.
     * @param spriteLabel The JLabel where the sprite will be displayed.
     */
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

    /**
     * Displays an error message in a dialog box.
     *
     * @param message The error message to display.
     */
    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}

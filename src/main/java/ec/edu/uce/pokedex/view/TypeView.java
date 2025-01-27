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
 * La clase TypeView es la encargada de mostrar los Pokémon por tipo en la interfaz de usuario.
 * Permite al usuario buscar Pokémon por tipo y ver los resultados en un panel desplazable.
 */
@Component
public class TypeView {

    @Getter
    private final JPanel panel;
    private final TypeController controller;
    private final UIConfig uiConfig;

    /**
     * Constructor para la clase TypeView.
     * Inicializa la vista y configura los componentes y los detectores de eventos.
     *
     * @param controller El controlador responsable de obtener los datos de Pokémon por tipo.
     * @param uiConfig La configuración de la interfaz de usuario para los componentes de estilo.
     */
    public TypeView(TypeController controller, UIConfig uiConfig) {
        this.controller = controller;
        this.uiConfig = uiConfig;
        this.panel = new JPanel(new BorderLayout(10, 10));
        initialize();
    }

    /**
     * Inicializa los componentes de la interfaz de usuario de TypeView, incluidos el título, la barra de búsqueda y el panel de resultados.
     * También establece el detector de acciones para el botón de búsqueda.
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
     * Crea y da estilo al campo de entrada de búsqueda.
     *
     * @return Un JTextField con estilo para buscar Pokémon por tipo.
     */
    private JTextField createSearchField() {
        JTextField searchField = new JTextField(25);
        searchField.setFont(uiConfig.labelFont());
        searchField.setHorizontalAlignment(JTextField.CENTER);
        searchField.setBorder(BorderFactory.createLineBorder(uiConfig.primaryColor(), 2));
        return searchField;
    }

    /**
     * Crea el panel de desplazamiento para mostrar Pokémon por tipo, que incluye un diseño para las cartas.
     *
     * @return Un JScrollPane que contiene un panel para mostrar Pokémon.
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
     * Obtiene y muestra la lista de Pokémon de un tipo determinado.
     * Este método se ejecuta de forma asincrónica para evitar bloquear la interfaz de usuario.
     *
     * @param typeName El tipo de Pokémon que se buscará (p. ej., "Fuego", "Agua").
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
     * Rellena el panel de visualización de Pokémon con cartas de Pokémon.
     *
     * @param pokemons Una lista de Pokémon que se mostrarán en la interfaz de usuario.
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
     * Crea una tarjeta de panel para un Pokémon que muestra su nombre y sprite.
     *
     * @param pokemon El Pokémon para el que se creará la tarjeta.
     * @return Un JPanel que representa la tarjeta de Pokémon con su nombre y sprite.
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
     * Carga una imagen de forma asincrónica para mostrar el sprite del Pokémon.
     *
     * @param spriteUrl La URL de la imagen del sprite del Pokémon.
     * @param spriteLabel La JLabel donde se mostrará el sprite.
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
     * Muestra un mensaje de error en un cuadro de diálogo.
     *
     * @param message El mensaje de error que se mostrará.
     */
    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}

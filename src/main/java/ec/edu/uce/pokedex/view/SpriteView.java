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
 * La clase SpriteView es responsable de mostrar la interfaz de usuario para buscar y visualizar sprites de Pokémon.
 * Permite a los usuarios buscar un Pokémon por nombre y ver varias imágenes de sprites (frontal, posterior, shiny, etc.).
 */
@Component
public class SpriteView {

    @Getter
    private final JPanel panel;
    private final SpriteController controller;
    private final UIConfig uiConfig;

    /**
     * Constructor de SpriteView.
     * Inicializa los componentes de la interfaz de usuario y configura los detectores de eventos.
     *
     * @param controller El controlador responsable de obtener los datos de los sprites.
     * @param uiConfig La configuración para el estilo de la interfaz de usuario.
     */
    public SpriteView(SpriteController controller, UIConfig uiConfig) {
        this.controller = controller;
        this.uiConfig = uiConfig;
        this.panel = new JPanel(new BorderLayout(10, 10));
        initialize();
    }

    /**
     * Inicializa los componentes de la interfaz de usuario, incluidos el título, la barra de búsqueda y el panel de sprites.
     * Configura el diseño y los detectores de eventos para el botón de búsqueda.
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
     * Crea y devuelve un JTextField para la barra de búsqueda.
     *
     * @return El componente JTextField para ingresar nombres de Pokémon.
     */
    private JTextField createSearchField() {
        JTextField searchField = new JTextField(20);
        searchField.setFont(uiConfig.labelFont());
        searchField.setHorizontalAlignment(JTextField.CENTER);
        searchField.setBorder(BorderFactory.createLineBorder(uiConfig.primaryColor(), 2));
        return searchField;
    }

    /**
     * Crea y devuelve un JButton para la función de búsqueda.
     *
     * @return El componente JButton para iniciar una búsqueda de sprites de Pokémon.
     */
    private JButton createSearchButton() {
        return ComponentFactory.createButton("Search", 16, uiConfig.primaryColor(), uiConfig.secondaryColor());
    }

    /**
     * Obtiene los sprites de Pokémon de forma asincrónica en función del nombre proporcionado y los muestra en el panel de sprites.
     *
     * @param pokemonName El nombre del Pokémon que se buscará.
     * @param spritePanel El panel donde se mostrarán los sprites.
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
     * Carga y devuelve una lista de ImageIcons en función de las URL de sprites proporcionadas.
     *
     * @param sprites El objeto Sprites que contiene las URL de varios tipos de sprites.
     * @return Una lista de ImageIcons para los sprites válidos.
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
     * Agrega un sprite a la lista de ImageIcons si la URL del sprite es válida.
     *
     * @param spriteIcons La lista de ImageIcons a la que se agregará el sprite.
     * @param spriteUrl La URL de la imagen del sprite que se cargará.
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
     * Actualiza el panel de sprites con los íconos de sprites indicados.
     * Organiza los íconos en un diseño de cuadrícula con etiquetas para cada tipo de sprite.
     *
     * @param spriteIcons La lista de ImageIcons para mostrar.
     * @param spritePanel El panel para actualizar con los íconos de sprites.
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
     * Muestra un mensaje de error en un cuadro de diálogo emergente.
     *
     * @param message El mensaje de error que se mostrará.
     */
    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}

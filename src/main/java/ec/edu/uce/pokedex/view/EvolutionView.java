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
 * La clase EvolutionView es responsable de gestionar la interfaz de usuario relacionada con la visualización de la cadena de evolución de un Pokémon.
 * Permite a los usuarios buscar una especie de Pokémon por nombre y ver sus etapas evolutivas.
 */
@Component
public class EvolutionView {

    @Getter
    private final JPanel panel;
    private final EvolutionController controller;
    private final UIConfig uiConfig;

    /**
     * Constructor de la clase EvolutionView.
     * Inicializa los componentes de la vista y configura los detectores de eventos.
     *
     * @param controller El controlador responsable de obtener los datos de la cadena de evolución.
     * @param uiConfig La configuración para el estilo de la interfaz de usuario.
     */
    public EvolutionView(EvolutionController controller, UIConfig uiConfig) {
        this.controller = controller;
        this.uiConfig = uiConfig;
        this.panel = new JPanel(new BorderLayout());
        initialize();
    }

    /**
     * Inicializa los componentes de EvolutionView.
     * Configura el título, la barra de búsqueda y el área de visualización de Evolution.
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
     * Crea el campo de búsqueda con estilo.
     *
     * @return Un JTextField que permite a los usuarios ingresar el nombre de una especie de Pokémon.
     */
    private JTextField createSearchField() {
        JTextField searchField = new JTextField(25);
        searchField.setFont(uiConfig.labelFont());
        searchField.setHorizontalAlignment(JTextField.CENTER);
        searchField.setBorder(BorderFactory.createLineBorder(uiConfig.primaryColor(), 2));
        return searchField;
    }

    /**
     * Obtiene la cadena de evolución de la especie de Pokémon indicada y la muestra en el panel proporcionado.
     * Esto se hace de forma asincrónica para evitar bloquear la interfaz de usuario.
     *
     * @paramspeciesName El nombre de la especie de Pokémon para la que se obtendrá la cadena de evolución.
     * @param evolutionPanel El panel donde se mostrará la cadena de evolución.
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
     * Rellena el panel de evolución con las etapas de evolución.
     *
     * @param chain La lista de etapas de evolución que se mostrarán.
     * @param evolutionPanel El panel que se rellenará con los detalles de la evolución.
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
     * Crea un panel para mostrar una etapa de evolución específica.
     *
     * @param stage Un mapa que contiene los datos de la etapa de evolución.
     * @return Un JPanel que muestra la etapa de evolución.
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
     * Crea un JLabel para el sprite de Pokémon, que muestra la imagen o un mensaje de error si no se puede cargar la imagen.
     *
     * @param imageUrl La URL de la imagen del sprite de Pokémon.
     * @return Un JLabel que muestra el sprite o un mensaje de error.
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
     * Extrae el ID del Pokémon de la URL de la especie.
     *
     * @param url La URL de los datos de la especie.
     * @return El ID del Pokémon extraído.
     */
    private String extractIdFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }

    /**
     * Muestra un mensaje de error al usuario en un cuadro de diálogo.
     *
     * @param message El mensaje de error que se mostrará.
     */
    private void showErrorMessage(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}

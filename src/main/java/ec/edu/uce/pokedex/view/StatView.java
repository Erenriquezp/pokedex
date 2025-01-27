package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.config.UIConfig;
import ec.edu.uce.pokedex.controller.StatController;
import ec.edu.uce.pokedex.exception.StatFetchException;
import ec.edu.uce.pokedex.models.Stat;
import ec.edu.uce.pokedex.util.ComponentFactory;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * La clase StatView es responsable de administrar la interfaz de usuario relacionada con la visualización de las estadísticas de los Pokémon.
 * Permite a los usuarios buscar un Pokémon por nombre y ver sus estadísticas en una tabla.
 */
@Component
public class StatView {

    @Getter
    private final JPanel panel;
    private final StatController controller;
    private final JTable statTable;
    private final UIConfig uiConfig;

    /**
     * Constructor para la clase StatView.
     * Inicializa los componentes de la vista y configura los detectores de eventos.
     *
     * @param controller El controlador responsable de obtener los datos de estadísticas de Pokémon.
     * @param uiConfig La configuración para el estilo de la interfaz de usuario.
     */
    public StatView(StatController controller, UIConfig uiConfig) {
        this.controller = controller;
        this.uiConfig = uiConfig;
        this.panel = new JPanel(new BorderLayout(10, 10));
        this.statTable = new JTable();
        initialize();
    }

    /**
     * Inicializa los componentes de StatView.
     * Configura el título, la barra de búsqueda y el área de visualización de la tabla de estadísticas.
     */
    private void initialize() {
        // Create title label
        JLabel titleLabel = ComponentFactory.createLabel("Pokémon Stats Viewer", 28, SwingConstants.CENTER);

        // Create search field and button
        JTextField searchField = ComponentFactory.createTextField(20, SwingConstants.CENTER);
        JButton searchButton = ComponentFactory.createButton("Search", 16, uiConfig.primaryColor(), uiConfig.secondaryColor());

        // Panel to hold search field and button
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchField.setBorder(BorderFactory.createLineBorder(uiConfig.primaryColor(), 2));

        // Scroll pane for the stats table
        JScrollPane scrollPane = new JScrollPane(statTable);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

        // Set action listener for search button
        searchButton.addActionListener(e -> handleSearch(searchField));
    }

    /**
     * Maneja la acción de búsqueda obteniendo y mostrando las estadísticas del Pokémon especificado.
     *
     * @param searchField El campo de búsqueda del cual se recupera el nombre del Pokémon.
     */
    private void handleSearch(JTextField searchField) {
        String pokemonName = searchField.getText().trim();
        if (pokemonName.isEmpty()) {
            showErrorMessage("Please enter a Pokémon name.");
            return;
        }
        fetchAndDisplayStats(pokemonName);
    }

    /**
     * Obtiene las estadísticas del Pokémon indicado y actualiza la pantalla.
     * Esto se hace de forma asincrónica para evitar bloquear la interfaz de usuario.
     *
     * @param pokemonName El nombre del Pokémon del que se obtendrán las estadísticas.
     */
    private void fetchAndDisplayStats(String pokemonName) {
        SwingWorker<List<Stat>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Stat> doInBackground() {
                try {
                    return controller.getStatsForPokemon(pokemonName);
                } catch (Exception e) {
                    throw new StatFetchException("Failed to fetch stats for Pokémon: " + pokemonName, e);
                }
            }

            @Override
            protected void done() {
                try {
                    List<Stat> stats = get();
                    if (stats.isEmpty()) {
                        showInfoMessage("No stats found for Pokémon: " + pokemonName);
                    } else {
                        populateTable(stats);
                    }
                } catch (StatFetchException e) {
                    showErrorMessage(e.getMessage());
                } catch (Exception e) {
                    showErrorMessage("Unexpected error occurred while fetching stats.");
                }
            }
        };
        worker.execute();
    }

    /**
     * Rellena la tabla de estadísticas con las estadísticas recuperadas del Pokémon.
     *
     * @param stats Lista de objetos Stat que representan las estadísticas de un Pokémon.
     */
    private void populateTable(List<Stat> stats) {
        SwingUtilities.invokeLater(() -> {
            String[] columnNames = {"Name", "Base Stat", "Effort"};
            Object[][] data = stats.stream()
                    .map(stat -> new Object[]{stat.getName(), stat.getBaseStat(), stat.getEffort()})
                    .toArray(Object[][]::new);

            statTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
        });
    }

    /**
     * Muestra un mensaje de error en un cuadro de diálogo.
     *
     * @param message El mensaje de error que se mostrará.
     */
    private void showErrorMessage(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }

    /**
     * Muestra un mensaje informativo en un cuadro de diálogo.
     *
     * @param message El mensaje informativo que se mostrará.
     */
    private void showInfoMessage(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Info", JOptionPane.INFORMATION_MESSAGE)
        );
    }
}

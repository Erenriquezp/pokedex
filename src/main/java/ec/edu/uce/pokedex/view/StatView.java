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
 * The StatView class is responsible for managing the user interface related to displaying Pokémon statistics.
 * It allows users to search for a Pokémon by name and view its stats in a table.
 */
@Component
public class StatView {

    @Getter
    private final JPanel panel;
    private final StatController controller;
    private final JTable statTable;
    private final UIConfig uiConfig;

    /**
     * Constructor for the StatView class.
     * Initializes the view components and sets up the event listeners.
     *
     * @param controller The controller responsible for fetching stats data for Pokémon.
     * @param uiConfig   The configuration for UI styling.
     */
    public StatView(StatController controller, UIConfig uiConfig) {
        this.controller = controller;
        this.uiConfig = uiConfig;
        this.panel = new JPanel(new BorderLayout(10, 10));
        this.statTable = new JTable();
        initialize();
    }

    /**
     * Initializes the components of the StatView.
     * Sets up the title, search bar, and stats table display area.
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
     * Handles the search action by fetching and displaying the stats for the specified Pokémon.
     *
     * @param searchField The search field from which the Pokémon name is retrieved.
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
     * Fetches the stats for the given Pokémon and updates the display.
     * This is done asynchronously to prevent blocking the UI.
     *
     * @param pokemonName The name of the Pokémon to fetch stats for.
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
     * Populates the stats table with the retrieved Pokémon stats.
     *
     * @param stats List of Stat objects representing the stats of a Pokémon.
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
     * Displays an error message in a dialog box.
     *
     * @param message The error message to display.
     */
    private void showErrorMessage(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }

    /**
     * Displays an informational message in a dialog box.
     *
     * @param message The information message to display.
     */
    private void showInfoMessage(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Info", JOptionPane.INFORMATION_MESSAGE)
        );
    }
}

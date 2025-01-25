package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.config.UIConfig;
import ec.edu.uce.pokedex.controller.StatController;
import ec.edu.uce.pokedex.models.Stat;
import ec.edu.uce.pokedex.util.ComponentFactory;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Component
public class StatView {

    @Getter
    private final JPanel panel;

    private final StatController controller;
    private final JTable statTable;
    private final UIConfig uiConfig;

    public StatView(StatController controller, UIConfig uiConfig) {
        this.controller = controller;
        this.uiConfig = uiConfig;
        this.panel = new JPanel(new BorderLayout(10, 10));
        this.statTable = new JTable();
        initialize();
    }

    /**
     * Initializes the components of the StatView.
     */
    private void initialize() {
        JLabel titleLabel = ComponentFactory.createLabel("Pokémon Stats Viewer", 24, SwingConstants.CENTER);

        JTextField searchField = ComponentFactory.createTextField(20, SwingConstants.CENTER);
        JButton searchButton = ComponentFactory.createButton("Search", 16, uiConfig.primaryColor(), uiConfig.secondaryColor());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JScrollPane scrollPane = new JScrollPane(statTable);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> handleSearch(searchField));
    }

    /**
     * Handles the search action to fetch stats from the database.
     *
     * @param searchField Field where the Pokémon name is entered.
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
     * Fetches and displays stats for a Pokémon.
     *
     * @param pokemonName Name of the Pokémon.
     */
    private void fetchAndDisplayStats(String pokemonName) {
        try {
            List<Stat> stats = controller.getStatsForPokemon(pokemonName);
            populateTable(stats);
        } catch (Exception e) {
            showErrorMessage("Error: Unable to fetch stats from the database.");
        }
    }

    /**
     * Populates the stats table with the given data.
     *
     * @param stats List of Stat objects.
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
     * Displays an error message to the user.
     *
     * @param message Error message to display.
     */
    private void showErrorMessage(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }

    /**
     * Displays an informational message to the user.
     *
     * @param message Informational message to display.
     */
    private void showInfoMessage(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Info", JOptionPane.INFORMATION_MESSAGE)
        );
    }
}

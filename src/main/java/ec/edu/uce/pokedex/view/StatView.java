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

    private void initialize() {
        JLabel titleLabel = ComponentFactory.createLabel("Pokémon Stats Viewer", 28, SwingConstants.CENTER);

        JTextField searchField = ComponentFactory.createTextField(20, SwingConstants.CENTER);
        JButton searchButton = ComponentFactory.createButton("Search", 16, uiConfig.primaryColor(), uiConfig.secondaryColor());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchField.setBorder(BorderFactory.createLineBorder(uiConfig.primaryColor(), 2));

        JScrollPane scrollPane = new JScrollPane(statTable);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> handleSearch(searchField));
    }

    private void handleSearch(JTextField searchField) {
        String pokemonName = searchField.getText().trim();
        if (pokemonName.isEmpty()) {
            showErrorMessage("Please enter a Pokémon name.");
            return;
        }
        fetchAndDisplayStats(pokemonName);
    }

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

    private void populateTable(List<Stat> stats) {
        SwingUtilities.invokeLater(() -> {
            String[] columnNames = {"Name", "Base Stat", "Effort"};
            Object[][] data = stats.stream()
                    .map(stat -> new Object[]{stat.getName(), stat.getBaseStat(), stat.getEffort()})
                    .toArray(Object[][]::new);

            statTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
        });
    }

    private void showErrorMessage(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }

    private void showInfoMessage(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Info", JOptionPane.INFORMATION_MESSAGE)
        );
    }
}

package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.controller.StatController;
import ec.edu.uce.pokedex.models.Stat;
import lombok.Getter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Component
public class StatView {

    /**
     * -- GETTER --
     *  Returns the main panel for this view.
     *
     * @return JPanel containing the stats view.
     */
    @Getter
    private final JPanel panel;
    private final StatController controller;
    private final JTable statTable;

    public StatView(StatController controller) {
        this.controller = controller;
        this.panel = new JPanel(new BorderLayout());
        this.statTable = new JTable();
        initialize();
    }

    /**
     * Initializes the components of the StatView.
     */
    private void initialize() {
        JLabel titleLabel = new JLabel("Pokémon Stats Viewer", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel searchPanel = new JPanel();
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Configurar tabla para mostrar estadísticas
        JScrollPane scrollPane = new JScrollPane(statTable);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> {
            String pokemonName = searchField.getText().trim();
            if (pokemonName.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter a Pokémon name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            fetchAndDisplayStats(pokemonName);
        });
    }

    /**
     * Fetches and displays the stats for a given Pokémon.
     *
     * @param pokemonName Name of the Pokémon.
     */
    private void fetchAndDisplayStats(String pokemonName) {
        controller.getStatsForPokemon(pokemonName)
                .collectList()
                .doOnNext(this::populateTable)
                .doOnError(err -> SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(panel, "Error: Unable to fetch stats.", "Error", JOptionPane.ERROR_MESSAGE)
                ))
                .subscribe();
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

}

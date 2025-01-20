package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.controller.MoveController;
import ec.edu.uce.pokedex.models.Move;
import ec.edu.uce.pokedex.models.VersionGroupDetail;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Component
public class MoveView {

    private final JPanel panel;
    private final MoveController controller;
    private final JTable moveTable;

    public MoveView(MoveController controller) {
        this.controller = controller;
        this.panel = new JPanel(new BorderLayout());
        this.moveTable = new JTable();
        initialize();
    }

    /**
     * Initializes the components of the MoveView.
     */
    private void initialize() {
        JLabel titleLabel = new JLabel("Pokémon Moves Viewer", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel searchPanel = new JPanel();
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JScrollPane scrollPane = new JScrollPane(moveTable);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> {
            String pokemonName = searchField.getText().trim();
            if (pokemonName.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter a Pokémon name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            fetchAndDisplayMoves(pokemonName);
        });
    }

    /**
     * Fetches and displays moves for a given Pokémon.
     *
     * @param pokemonName Name of the Pokémon.
     */
    private void fetchAndDisplayMoves(String pokemonName) {
        controller.getMovesForPokemon(pokemonName)
                .collectList()
                .doOnNext(this::populateTable)
                .doOnError(err -> SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(panel, "Error: Unable to fetch moves.", "Error", JOptionPane.ERROR_MESSAGE)
                ))
                .subscribe();
    }

    /**
     * Populates the table with move data.
     *
     * @param moves List of Move objects.
     */
    private void populateTable(List<Move> moves) {
        SwingUtilities.invokeLater(() -> {
            String[] columnNames = {"Name", "Learn Method", "Level", "Version Group"};
            Object[][] data = moves.stream()
                    .flatMap(move -> move.getVersionGroupDetails().stream().map(detail -> new Object[]{
                            move.getName(),
                            detail.getMoveLearnMethod(),
                            detail.getLevelLearnedAt(),
                            detail.getVersionGroup()
                    }))
                    .toArray(Object[][]::new);

            moveTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
        });
    }

    /**
     * Returns the main panel for this view.
     *
     * @return JPanel containing the move view.
     */
    public JPanel getPanel() {
        return panel;
    }
}

package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.controller.TypeController;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Component
public class TypeView {

    /**
     * -- GETTER --
     *  Returns the main panel for this view.
     *
     */
    @Getter
    private final JPanel panel;
    private final TypeController controller;
    private final JList<String> pokemonList;

    public TypeView(TypeController controller) {
        this.controller = controller;
        this.panel = new JPanel(new BorderLayout());
        this.pokemonList = new JList<>();
        initialize();
    }

    /**
     * Initializes the components of the TypeView.
     */
    private void initialize() {
        JLabel titleLabel = new JLabel("Pokémon by Type Viewer", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel searchPanel = new JPanel();
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Configurar lista para mostrar los Pokémon
        JScrollPane scrollPane = new JScrollPane(pokemonList);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> {
            String typeName = searchField.getText().trim();
            if (typeName.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter a type name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            fetchAndDisplayPokemonByType(typeName);
        });
    }

    /**
     * Fetches and displays Pokémon by type.
     *
     * @param typeName Name of the type.
     */
    private void fetchAndDisplayPokemonByType(String typeName) {
        controller.getPokemonByType(typeName)
                .collectList()
                .doOnNext(this::populateList)
                .doOnError(err -> SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(panel, "Error: Unable to fetch Pokémon by type.", "Error", JOptionPane.ERROR_MESSAGE)
                ))
                .subscribe();
    }

    /**
     * Populates the list with Pokémon names.
     *
     * @param pokemonNames List of Pokémon names.
     */
    private void populateList(List<String> pokemonNames) {
        SwingUtilities.invokeLater(() -> {
            if (pokemonNames.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "No Pokémon found for this type.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
            String[] data = pokemonNames.toArray(String[]::new); // Usar stream para convertir la lista
            pokemonList.setListData(data);
        });
    }

}

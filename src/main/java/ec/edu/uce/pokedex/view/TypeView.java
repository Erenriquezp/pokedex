package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.config.UIConfig;
import ec.edu.uce.pokedex.controller.TypeController;
import ec.edu.uce.pokedex.models.Pokemon;
import ec.edu.uce.pokedex.util.ComponentFactory;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TypeView {

    @Getter
    private final JPanel panel;
    private final TypeController controller;
    private final JList<String> pokemonList;
    private final UIConfig uiConfig;

    public TypeView(TypeController controller, UIConfig uiConfig) {
        this.controller = controller;
        this.uiConfig = uiConfig;
        this.panel = new JPanel(new BorderLayout());
        this.pokemonList = new JList<>();
        initialize();
    }

    private void initialize() {
        JLabel titleLabel = ComponentFactory.createLabel("Pokémon by Type Viewer", 24, SwingConstants.CENTER);

        JTextField searchField = ComponentFactory.createTextField(20, JTextField.CENTER);
        JButton searchButton = ComponentFactory.createButton("Search", 16, uiConfig.primaryColor(), uiConfig.secondaryColor());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JScrollPane scrollPane = ComponentFactory.createScrollPane(pokemonList);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> {
            String typeName = searchField.getText().trim();
            if (typeName.isEmpty()) {
                showError("Please enter a type name.");
                return;
            }
            fetchAndDisplayPokemonByType(typeName);
        });
    }

    private void fetchAndDisplayPokemonByType(String typeName) {
        try {
            List<String> pokemonNames = controller.getPokemonByType(typeName).stream().map(Pokemon::getName).collect(Collectors.toList());
            System.out.println(pokemonNames);
            populateList(pokemonNames);
        } catch (Exception e) {
            showError("Error: Unable to fetch Pokémon by type.");
        }
    }

    private void populateList(List<String> pokemonNames) {
        SwingUtilities.invokeLater(() -> {
            if (pokemonNames.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "No Pokémon found for this type.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
            pokemonList.setListData(pokemonNames.toArray(String[]::new));
        });
    }

    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}

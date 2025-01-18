package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.controller.AbilityController;
import ec.edu.uce.pokedex.models.Ability;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Component
public class AbilityView {

    private final JPanel panel;
    private final AbilityController controller;

    public AbilityView(AbilityController controller) {
        this.controller = controller;
        this.panel = new JPanel(new BorderLayout());
        initialize();
    }

    /**
     * Initializes the components of the AbilityView.
     */
    private void initialize() {
        JLabel titleLabel = new JLabel("Search Pokémon Abilities", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JList<String> abilityList = new JList<>();
        JScrollPane scrollPane = new JScrollPane(abilityList);

        searchButton.addActionListener(e -> {
            String pokemonName = searchField.getText().trim();
            if (pokemonName.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter a Pokémon name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            fetchAndDisplayAbilities(pokemonName, abilityList);
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(searchField);
        inputPanel.add(searchButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
    }

    /**
     * Fetches and displays the abilities of the given Pokémon in the JList.
     *
     * @param pokemonName Name of the Pokémon.
     * @param abilityList JList to display the abilities.
     */
    private void fetchAndDisplayAbilities(String pokemonName, JList<String> abilityList) {
        controller.getAbilitiesForPokemon(pokemonName)
                .map(Ability::getName)
                .collectList()
                .doOnNext(abilities -> SwingUtilities.invokeLater(() -> {
                    if (abilities.isEmpty()) {
                        JOptionPane.showMessageDialog(panel, "No abilities found for this Pokémon.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        abilityList.setListData(abilities.toArray(new String[0]));
                    }
                }))
                .doOnError(err -> SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(panel, "Error: Unable to fetch abilities.", "Error", JOptionPane.ERROR_MESSAGE)
                ))
                .subscribe();
    }

    /**
     * Returns the main panel for this view.
     *
     * @return JPanel containing the ability view.
     */
    public JPanel getPanel() {
        return panel;
    }
}

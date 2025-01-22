package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.controller.AbilityController;
import ec.edu.uce.pokedex.models.Ability;
import ec.edu.uce.pokedex.models.Pokemon;
import ec.edu.uce.pokedex.service.PokeService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

@Component
public class AbilityView {

    private final JPanel panel;
    private final AbilityController controller;
    private final PokeService pokeService;
    private final JLabel imageLabel;

    public AbilityView(AbilityController controller, PokeService pokeService) {
        this.controller = controller;
        this.pokeService = pokeService;
        this.panel = new JPanel(new BorderLayout());
        this.imageLabel = new JLabel("", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(400, 400)); // Larger image size
        initialize();
    }

    /**
     * Initializes the components of the AbilityView.
     */
    private void initialize() {
        JLabel titleLabel = new JLabel("Search Pokémon Abilities", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JList<String> abilityList = new JList<>();
        abilityList.setFont(new Font("Arial", Font.PLAIN, 18)); // Larger font for abilities
        JScrollPane scrollPane = new JScrollPane(abilityList);

        searchButton.addActionListener(e -> {
            String pokemonName = searchField.getText().trim().toLowerCase();
            if (pokemonName.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter a Pokémon name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            fetchAndDisplayAbilities(pokemonName, abilityList);
            fetchPokemonImage(pokemonName, imageLabel);
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(searchField);
        inputPanel.add(searchButton);

        JPanel displayPanel = new JPanel(new BorderLayout(10, 10));
        displayPanel.add(imageLabel, BorderLayout.CENTER);
        displayPanel.add(scrollPane, BorderLayout.SOUTH);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(displayPanel, BorderLayout.SOUTH);
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
     * Fetches and displays the Pokémon image using data from the PokeService.
     *
     * @param pokemonName      Name of the Pokémon.
     * @param pokemonImageLabel JLabel to display the Pokémon image.
     */
    private void fetchPokemonImage(String pokemonName, JLabel pokemonImageLabel) {
        pokeService.getPokemonByName(pokemonName)
                .doOnNext(pokemon -> SwingUtilities.invokeLater(() -> {
                    try {
                        URL spriteUrl = new URL(pokemon.getSprites().getFrontDefault());
                        ImageIcon spriteIcon = new ImageIcon(spriteUrl);
                        Image scaledImage = spriteIcon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH); // Larger image size
                        pokemonImageLabel.setIcon(new ImageIcon(scaledImage));
                        pokemonImageLabel.setText(""); // Clear error text if present
                    } catch (Exception e) {
                        pokemonImageLabel.setText("Image not available");
                        pokemonImageLabel.setIcon(null);
                    }
                }))
                .doOnError(err -> SwingUtilities.invokeLater(() -> {
                    pokemonImageLabel.setText("Image not available");
                    pokemonImageLabel.setIcon(null);
                }))
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

package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.controller.SpriteController;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

@Component
public class SpriteView {

    /**
     * -- GETTER --
     *  Returns the main panel for this view.
     *
     */
    @Getter
    private final JPanel panel;
    private final SpriteController controller;

    public SpriteView(SpriteController controller) {
        this.controller = controller;
        this.panel = new JPanel(new BorderLayout());
        initialize();
    }

    /**
     * Initializes the components of the SpriteView.
     */
    private void initialize() {
        JLabel titleLabel = new JLabel("Search Pokémon Sprites", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JPanel spritePanel = new JPanel(new GridLayout(2, 4, 10, 10));
        JScrollPane scrollPane = new JScrollPane(spritePanel);

        searchButton.addActionListener(e -> {
            String pokemonName = searchField.getText().trim();
            if (pokemonName.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter a Pokémon name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            fetchAndDisplaySprites(pokemonName, spritePanel);
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(searchField);
        inputPanel.add(searchButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
    }

    /**
     * Fetches and displays the sprites of the given Pokémon.
     *
     * @param pokemonName Name of the Pokémon.
     * @param spritePanel Panel where sprites will be displayed.
     */
    private void fetchAndDisplaySprites(String pokemonName, JPanel spritePanel) {
        controller.getSpritesForPokemon(pokemonName)
                .doOnNext(sprites -> SwingUtilities.invokeLater(() -> {
                    spritePanel.removeAll();
                    addSpriteToPanel(spritePanel, "Front Default", sprites.getFrontDefault());
                    addSpriteToPanel(spritePanel, "Back Default", sprites.getBackDefault());
                    addSpriteToPanel(spritePanel, "Front Shiny", sprites.getFrontShiny());
                    addSpriteToPanel(spritePanel, "Back Shiny", sprites.getBackShiny());
                    spritePanel.revalidate();
                    spritePanel.repaint();
                }))
                .doOnError(err -> SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(panel, "Error: Unable to fetch sprites.", "Error", JOptionPane.ERROR_MESSAGE)
                ))
                .subscribe();
    }

    /**
     * Adds a sprite image and label to the panel.
     *
     * @param spritePanel Panel where the sprite will be added.
     * @param label       Label for the sprite.
     * @param spriteUrl   URL of the sprite image.
     */
    private void addSpriteToPanel(JPanel spritePanel, String label, String spriteUrl) {
        if (spriteUrl != null && !spriteUrl.isEmpty()) {
            try {
                ImageIcon spriteIcon = new ImageIcon(new URL(spriteUrl));
                JLabel imageLabel = new JLabel(label, spriteIcon, SwingConstants.CENTER);
                imageLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
                imageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                spritePanel.add(imageLabel);
            } catch (Exception e) {
                spritePanel.add(new JLabel(label + ": Error loading image"));
            }
        } else {
            spritePanel.add(new JLabel(label + ": Not available"));
        }
    }

}

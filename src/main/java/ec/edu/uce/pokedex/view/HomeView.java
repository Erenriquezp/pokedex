package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.config.UIConfig;
import ec.edu.uce.pokedex.dto.PokemonDto;
import ec.edu.uce.pokedex.models.Ability;
import ec.edu.uce.pokedex.models.Type;
import ec.edu.uce.pokedex.service.PokeServiceDto;
import ec.edu.uce.pokedex.util.ComponentFactory;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

@Component
public class HomeView {

    @Getter
    private final JPanel panel;
    private final PokeServiceDto pokeServiceDto;
    private final UIConfig uiConfig;
    private int offset = 0; // Controla la posición actual de paginación

    public HomeView(PokeServiceDto pokeServiceDto, UIConfig uiConfig) {
        this.pokeServiceDto = pokeServiceDto;
        this.uiConfig = uiConfig;
        this.panel = new JPanel(new BorderLayout());
        initialize();
    }

    /**
     * Initializes the panel with paginated Pokémon examples.
     */
    private void initialize() {
        JLabel titleLabel = ComponentFactory.createLabel("Pokédex - Home", 24, SwingConstants.CENTER);

        JPanel pokemonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        JScrollPane scrollPane = new JScrollPane(pokemonPanel);

        JButton backButton = ComponentFactory.createButton("Back", 16, uiConfig.primaryColor(), uiConfig.secondaryColor());
        JButton nextButton = ComponentFactory.createButton("Next", 16, uiConfig.primaryColor(), uiConfig.secondaryColor());

        // Acción para retroceder
        backButton.addActionListener(e -> {
            if (offset > 0) {
                offset -= 6;
                loadPokemonPage(pokemonPanel);
            } else {
                showError("You're already on the first page.");
            }
        });

        // Acción para avanzar
        nextButton.addActionListener(e -> {
            offset += 6;
            loadPokemonPage(pokemonPanel);
        });

        // Panel inferior con botones de navegación
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        navigationPanel.add(backButton);
        navigationPanel.add(nextButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(navigationPanel, BorderLayout.SOUTH);

        loadPokemonPage(pokemonPanel);
    }

    /**
     * Loads a page of Pokémon from the database and populates the panel.
     *
     * @param pokemonPanel Panel to populate with Pokémon cards.
     */
    private void loadPokemonPage(JPanel pokemonPanel) {
        try {
            List<PokemonDto> pokemonDtos = pokeServiceDto.getPokemonPage(offset, 6);

            SwingUtilities.invokeLater(() -> {
                pokemonPanel.removeAll();
                pokemonDtos.forEach(dto -> pokemonPanel.add(createPokemonCard(dto)));
                pokemonPanel.revalidate();
                pokemonPanel.repaint();
            });
        } catch (Exception e) {
            showError("Failed to load Pokémon data: " + e.getMessage());
        }
    }

    /**
     * Creates a card for a Pokémon.
     *
     * @param dto The PokemonDto object.
     * @return JPanel representing the Pokémon card.
     */
    private JPanel createPokemonCard(PokemonDto dto) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createLineBorder(uiConfig.primaryColor(), 2));
        card.setBackground(uiConfig.secondaryColor());

        JLabel nameLabel = ComponentFactory.createLabel(dto.getName().toUpperCase(), 16, SwingConstants.CENTER);

        JLabel typeLabel = createTypeLabel(dto.getTypes().stream().map(Type::getName).distinct().toList());
        JLabel abilityLabel = createAbilityLabel(dto.getAbilities().stream().map(Ability::getName).distinct().toList());
        JLabel spriteLabel = createSpriteLabel(dto.getSpriteUrl());

        card.add(nameLabel, BorderLayout.NORTH);
        card.add(spriteLabel, BorderLayout.CENTER);

        JPanel detailsPanel = new JPanel(new GridLayout(2, 1));
        detailsPanel.add(typeLabel);
        detailsPanel.add(abilityLabel);
        detailsPanel.setBackground(uiConfig.secondaryColor());
        card.add(detailsPanel, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Creates a JLabel for the Pokémon sprite.
     *
     * @param spriteUrl URL of the Pokémon sprite.
     * @return JLabel with the sprite or an error message.
     */
    private JLabel createSpriteLabel(String spriteUrl) {
        JLabel spriteLabel = new JLabel();
        try {
            URI uri = new URI(spriteUrl);
            spriteLabel.setIcon(new ImageIcon(uri.toURL()));
        } catch (MalformedURLException e) {
            spriteLabel.setText("Invalid image URL");
            spriteLabel.setFont(uiConfig.labelFont());
        } catch (Exception e) {
            spriteLabel.setText("Failed to load image");
            spriteLabel.setFont(uiConfig.labelFont());
        }
        spriteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        return spriteLabel;
    }

    /**
     * Creates a JLabel for the Pokémon types.
     *
     * @param types List of types.
     * @return JLabel with the types' information.
     */
    private JLabel createTypeLabel(List<String> types) {
        String typeNames = String.join(", ", types);
        return ComponentFactory.createLabel("Types: " + typeNames, 16, SwingConstants.CENTER);
    }

    /**
     * Creates a JLabel for the Pokémon abilities.
     *
     * @param abilities List of abilities.
     * @return JLabel with the abilities' information.
     */
    private JLabel createAbilityLabel(List<String> abilities) {
        String abilityNames = String.join(", ", abilities);
        return ComponentFactory.createLabel("Abilities: " + abilityNames, 16, SwingConstants.CENTER);
    }

    /**
     * Displays an error message to the user.
     *
     * @param message Error message to display.
     */
    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}

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
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class HomeView {

    @Getter
    private final JPanel panel;
    private final PokeServiceDto pokeService;
    private final UIConfig uiConfig;

    public HomeView(PokeServiceDto pokeService, UIConfig uiConfig) {
        this.pokeService = pokeService;
        this.uiConfig = uiConfig;
        this.panel = new JPanel(new GridLayout(2, 3, 10, 10));
        initialize();
    }

    /**
     * Initializes the panel with Pokémon examples using PokemonDto.
     */
    private void initialize() {
        List<String> examplePokemon = List.of("charizard", "bulbasaur", "charmander", "squirtle", "venusaur", "blastoise");
        List<PokemonDto> pokemonDtos = examplePokemon.stream()
                .map(pokeService::getPokemonDtoByName) // Obtener Optional<PokemonDto>
                .flatMap(Optional::stream) // Filtrar los presentes y convertirlos en un Stream
                .collect(Collectors.toList());

        populatePanel(pokemonDtos);
    }

    /**
     * Populates the panel with Pokémon cards.
     *
     * @param pokemon List of PokemonDto objects.
     */
    private void populatePanel(List<PokemonDto> pokemon) {
        SwingUtilities.invokeLater(() -> {
            panel.removeAll();
            pokemon.stream()
                    .map(this::createPokemonCard)
                    .forEach(panel::add);
            panel.revalidate();
            panel.repaint();
        });
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

        JLabel typeLabel = createTypeLabel(dto.getTypes());
        JLabel abilityLabel = createAbilityLabel(dto.getAbilities());
        JLabel spriteLabel = createSpriteLabel(dto.getSpriteUrl());

        card.add(nameLabel, BorderLayout.NORTH);
        card.add(spriteLabel, BorderLayout.CENTER);

        // Crear un panel inferior para tipos y habilidades
        JPanel detailsPanel = new JPanel(new GridLayout(2, 1));
        detailsPanel.add(typeLabel);
        detailsPanel.add(abilityLabel);
        detailsPanel.setBackground(uiConfig.secondaryColor());
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
            ImageIcon spriteIcon = new ImageIcon(new URL(spriteUrl));
            Image scaledImage = spriteIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            spriteLabel.setIcon(new ImageIcon(scaledImage));
            spriteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (Exception e) {
            spriteLabel.setText("Image not available");
            spriteLabel.setHorizontalAlignment(SwingConstants.CENTER);
            spriteLabel.setFont(uiConfig.labelFont());
        }
        spriteLabel.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 10));

        return spriteLabel;
    }

    /**
     * Creates a JLabel for the Pokémon types.
     *
     * @param types List of types.
     * @return JLabel with the types' information.
     */
    private JLabel createTypeLabel(List<Type> types) {
        String typeNames = types.stream()
                .map(Type::getName)
                .collect(Collectors.joining(", "));
        return ComponentFactory.createLabel("Types: " + typeNames, 14, SwingConstants.CENTER);
    }

    /**
     * Creates a JLabel for the Pokémon abilities.
     *
     * @param abilities List of abilities.
     * @return JLabel with the abilities' information.
     */
    private JLabel createAbilityLabel(List<Ability> abilities) {
        String abilityNames = abilities.stream()
                .map(Ability::getName)
                .collect(Collectors.joining(", "));
        return ComponentFactory.createLabel("Abilities: " + abilityNames, 14, SwingConstants.CENTER);
    }
}

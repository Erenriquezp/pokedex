package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.dto.PokemonDto;
import ec.edu.uce.pokedex.service.PokeServiceDto;
import lombok.Getter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

@Component
public class ExamplePanel {

    /**
     * -- GETTER --
     * Returns the main panel for this view.
     */
    @Getter
    private final JPanel panel;
    private final PokeServiceDto pokeService;

    public ExamplePanel(PokeServiceDto pokeService) {
        this.pokeService = pokeService;
        this.panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2, 15, 15)); // Grid con más espacio
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Márgenes exteriores
        initialize();
    }

    /**
     * Initializes the panel with Pokémon examples using PokemonDto.
     */
    private void initialize() {
        List<String> examplePokemon = List.of("pikachu", "bulbasaur", "charmander", "squirtle");
        Flux.fromIterable(examplePokemon)
                .flatMap(pokeService::getPokemonDtoByName)
                .collectList()
                .doOnNext(this::populatePanel)
                .subscribe();
    }

    /**
     * Populates the panel with Pokémon cards.
     *
     * @param pokemons List of PokemonDto objects.
     */
    private void populatePanel(List<PokemonDto> pokemons) {
        SwingUtilities.invokeLater(() -> {
            panel.removeAll();
            pokemons.stream()
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
        card.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        card.setBackground(Color.WHITE);

        // Nombre del Pokémon
        JLabel nameLabel = new JLabel(dto.getName().toUpperCase(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nameLabel.setForeground(new Color(60, 80, 180));

        // Imagen del Pokémon
        JLabel spriteLabel = new JLabel();
        try {
            ImageIcon spriteIcon = new ImageIcon(new URL(dto.getSpriteUrl()));
            Image scaledImage = spriteIcon.getImage().getScaledInstance(260, 260, Image.SCALE_SMOOTH); // Imagen más grande
            spriteLabel.setIcon(new ImageIcon(scaledImage));
            spriteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (Exception e) {
            spriteLabel.setText("Image not available");
            spriteLabel.setHorizontalAlignment(SwingConstants.CENTER);
            spriteLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        }

        // Tipo del Pokémon
        JLabel typeLabel = new JLabel("Type: ", SwingConstants.CENTER);
        typeLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Negrilla para "Type:"
        typeLabel.setForeground(new Color(50, 50, 50));

        JLabel typeValueLabel = new JLabel(dto.getType(), SwingConstants.CENTER);
        typeValueLabel.setFont(new Font("Arial", Font.PLAIN, 14)); // Sin negrilla para el valor

        // Habilidad del Pokémon
        JLabel abilityLabel = new JLabel("Ability: ", SwingConstants.CENTER);
        abilityLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Negrilla para "Ability:"
        abilityLabel.setForeground(new Color(50, 50, 50));

        JLabel abilityValueLabel = new JLabel(dto.getAbility(), SwingConstants.CENTER);
        abilityValueLabel.setFont(new Font("Arial", Font.PLAIN, 14)); // Sin negrilla para el valor

        // Añadiendo componentes al diseño de la tarjeta
        card.add(nameLabel, BorderLayout.NORTH);
        card.add(spriteLabel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new GridLayout(2, 2));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(typeLabel);
        infoPanel.add(typeValueLabel);
        infoPanel.add(abilityLabel);
        infoPanel.add(abilityValueLabel);

        card.add(infoPanel, BorderLayout.SOUTH);
        return card;
    }
}

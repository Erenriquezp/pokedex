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
     *  Returns the main panel for this view.
     *
     */
    @Getter
    private final JPanel panel;
    private final PokeServiceDto pokeService;

    public ExamplePanel(PokeServiceDto pokeService) {
        this.pokeService = pokeService;
        this.panel = new JPanel(new GridLayout(2, 3, 10, 10));
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
            for (PokemonDto dto : pokemons) {
                panel.add(createPokemonCard(dto));
            }
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
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel nameLabel = new JLabel(dto.getName().toUpperCase(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel typeLabel = new JLabel("Type: " + dto.getType(), SwingConstants.CENTER);
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel abilityLabel = new JLabel("Ability: " + dto.getAbility(), SwingConstants.CENTER);
        abilityLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel spriteLabel = new JLabel();
        try {
            ImageIcon spriteIcon = new ImageIcon(new URL(dto.getSpriteUrl()));
            spriteLabel.setIcon(spriteIcon);
            spriteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (Exception e) {
            spriteLabel.setText("Image not available");
        }

        card.add(nameLabel, BorderLayout.NORTH);
        card.add(spriteLabel, BorderLayout.CENTER);
        card.add(typeLabel, BorderLayout.WEST);
        card.add(abilityLabel, BorderLayout.SOUTH);

        return card;
    }

}

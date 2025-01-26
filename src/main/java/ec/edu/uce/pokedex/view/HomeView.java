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
import java.net.URL;
import java.util.List;

@Component
public class HomeView {

    @Getter
    private final JPanel panel;
    private final PokeServiceDto pokeServiceDto;
    private final UIConfig uiConfig;
    private int offset = 0;

    public HomeView(PokeServiceDto pokeServiceDto, UIConfig uiConfig) {
        this.pokeServiceDto = pokeServiceDto;
        this.uiConfig = uiConfig;
        this.panel = new JPanel(new BorderLayout());
        initialize();
    }

    private void initialize() {
        JLabel titleLabel = ComponentFactory.createLabel("Pokédex - Home", 35, SwingConstants.CENTER);

        JPanel pokemonPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        JScrollPane scrollPane = ComponentFactory.createScrollPane(pokemonPanel);

        JButton backButton = ComponentFactory.createButton("Back", 18, uiConfig.primaryColor(), uiConfig.secondaryColor());
        JButton nextButton = ComponentFactory.createButton("Next", 18, uiConfig.primaryColor(), uiConfig.secondaryColor());

        backButton.addActionListener(e -> {
            if (offset > 0) {
                offset -= 6;
                loadPokemonPage(pokemonPanel);
            } else {
                showError("You're already on the first page.");
            }
        });

        nextButton.addActionListener(e -> {
            offset += 6;
            loadPokemonPage(pokemonPanel);
        });

        JPanel navigationPanel = ComponentFactory.createPanel(new FlowLayout(FlowLayout.CENTER), uiConfig.secondaryColor());
        navigationPanel.add(backButton);
        navigationPanel.add(nextButton);

        panel.setBackground(uiConfig.secondaryColor());
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(navigationPanel, BorderLayout.SOUTH);

        loadPokemonPage(pokemonPanel);
    }

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

    private JPanel createPokemonCard(PokemonDto dto) {
        JPanel card = ComponentFactory.createPanel(new BorderLayout(10, 10), uiConfig.secondaryColor());
        card.setBorder(BorderFactory.createLineBorder(uiConfig.tertiaryColor(), 3));

        JLabel nameLabel = ComponentFactory.createLabel(dto.getName().toUpperCase(), 25, SwingConstants.CENTER);

        JLabel spriteLabel = createSpriteLabel(dto.getSpriteUrl());

        JPanel typeLabel = createTypeLabel(dto.getTypes().stream().map(Type::getName).distinct().toList());
        JPanel abilityLabel = createAbilityLabel(dto.getAbilities().stream().map(Ability::getName).distinct().toList());

        JPanel detailsPanel = ComponentFactory.createPanel(new GridLayout(2, 1, 5, 5), uiConfig.secondaryColor());
        detailsPanel.add(typeLabel);
        detailsPanel.add(abilityLabel);

        card.add(nameLabel, BorderLayout.NORTH);
        card.add(spriteLabel, BorderLayout.CENTER);
        card.add(detailsPanel, BorderLayout.SOUTH);

        return card;
    }

    private JLabel createSpriteLabel(String spriteUrl) {
        JLabel spriteLabel = new JLabel();
        try {
            ImageIcon spriteIcon = new ImageIcon(new URL(spriteUrl));
            Image scaledImage = spriteIcon.getImage().getScaledInstance(170, 170, Image.SCALE_SMOOTH);
            spriteLabel.setIcon(new ImageIcon(scaledImage));
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

    private JPanel createTypeLabel(List<String> types) {
        JLabel typeTitleLabel = ComponentFactory.createLabel("Types: ", 18, SwingConstants.LEFT);
        typeTitleLabel.setForeground(uiConfig.primaryColor());

        JLabel typeContentLabel = ComponentFactory.createLabel(String.join(", ", types), 16, SwingConstants.LEFT);

        JPanel typePanel = ComponentFactory.createPanel(new BorderLayout(), uiConfig.secondaryColor());
        typePanel.add(typeTitleLabel, BorderLayout.NORTH);
        typePanel.add(typeContentLabel, BorderLayout.CENTER);

        return typePanel;
    }

    private JPanel createAbilityLabel(List<String> abilities) {
        JLabel abilityTitleLabel = ComponentFactory.createLabel("Abilities: ", 18, SwingConstants.LEFT);
        abilityTitleLabel.setForeground(uiConfig.primaryColor());

        JLabel abilityContentLabel = ComponentFactory.createLabel(String.join(", ", abilities), 16, SwingConstants.LEFT);

        JPanel abilityPanel = ComponentFactory.createPanel(new BorderLayout(), uiConfig.secondaryColor());
        abilityPanel.add(abilityTitleLabel, BorderLayout.NORTH);
        abilityPanel.add(abilityContentLabel, BorderLayout.CENTER);

        return abilityPanel;
    }

    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}

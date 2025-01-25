package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.config.UIConfig;
import ec.edu.uce.pokedex.models.Move;
import ec.edu.uce.pokedex.models.Pokemon;
import ec.edu.uce.pokedex.service.PokeService;
import ec.edu.uce.pokedex.util.ComponentFactory;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

@Component
public class SearchView {

    @Getter
    private final JPanel panel;
    private final JLabel imageLabel;
    private final JLabel infoLabel;
    private final UIConfig uiConfig;
    private final JList<String> abilityList;
    private final JList<String> moveList;
    private final JTextField searchField;

    public SearchView(PokeService pokeService, UIConfig uiConfig) {
        this.uiConfig = uiConfig;
        this.panel = new JPanel(new BorderLayout(10, 10));

        // Crear componentes principales
        JLabel titleLabel = ComponentFactory.createLabel("Search Pokémon", 24, SwingConstants.CENTER);
        this.searchField = createSearchField();
        JButton searchButton = createSearchButton(pokeService);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        this.imageLabel = ComponentFactory.createLabel("", 0, SwingConstants.CENTER);
        this.imageLabel.setPreferredSize(new Dimension(250, 250));
        this.abilityList = new JList<>();
        this.moveList = new JList<>();

        this.infoLabel = ComponentFactory.createLabel("Enter a Pokémon name to search", 14, SwingConstants.CENTER);
        this.infoLabel.setFont(uiConfig.labelFont());

        JPanel displayPanel = createDisplayPanel();
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(displayPanel, BorderLayout.SOUTH);
    }

    private JTextField createSearchField() {
        JTextField searchField = new JTextField(20);
        searchField.setFont(uiConfig.labelFont());
        searchField.setHorizontalAlignment(JTextField.CENTER);
        return searchField;
    }

    private JButton createSearchButton(PokeService pokeService) {
        JButton searchButton = ComponentFactory.createButton("Search", 16, uiConfig.primaryColor(), uiConfig.secondaryColor());
        searchButton.addActionListener(e -> {
            String name = searchField.getText().trim();
            if (name.isEmpty()) {
                showError("Please enter a valid Pokémon name.");
                return;
            }
            try {
                fetchAndDisplayPokemonInfo(pokeService, name);
            } catch (Exception ex) {
                showError("An unexpected error occurred: " + ex.getMessage());
            }
        });
        return searchButton;
    }

    private JPanel createDisplayPanel() {
        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.PAGE_AXIS));
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel imageAndInfoPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(infoLabel, BorderLayout.CENTER);

        imageAndInfoPanel.add(imagePanel);
        imageAndInfoPanel.add(infoPanel);

        displayPanel.add(imageAndInfoPanel);

        JPanel abilitiesAndMovesPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        JPanel abilityPanel = new JPanel(new BorderLayout());
        JLabel abilityTitle = ComponentFactory.createLabel("Abilities", 20, SwingConstants.CENTER);
        abilityPanel.add(abilityTitle, BorderLayout.NORTH);
        abilityPanel.add(ComponentFactory.createScrollPane(abilityList), BorderLayout.CENTER);

        JPanel movePanel = new JPanel(new BorderLayout());
        JLabel moveTitle = ComponentFactory.createLabel("Moves", 20, SwingConstants.CENTER);
        movePanel.add(moveTitle, BorderLayout.NORTH);
        movePanel.add(ComponentFactory.createScrollPane(moveList), BorderLayout.CENTER);

        abilitiesAndMovesPanel.add(abilityPanel);
        abilitiesAndMovesPanel.add(movePanel);

        displayPanel.add(abilitiesAndMovesPanel);

        return displayPanel;
    }

    private void fetchAndDisplayPokemonInfo(PokeService pokeService, String name) {
        pokeService.getPokemonByName(name.toLowerCase())
                .ifPresentOrElse(this::displayPokemonInfo, () -> showError("Pokémon not found."));
    }

    private void displayPokemonInfo(Pokemon pokemon) {
        SwingUtilities.invokeLater(() -> {
            try {
                String info = String.format(
                        "<html><h2>Pokemon Found!</h2>" +
                                "<b>ID:</b> %d<br>" +
                                "<b>Name:</b> %s<br>" +
                                "<b>Base Experience:</b> %d<br>" +
                                "<b>Height:</b> %d<br>" +
                                "<b>Weight:</b> %d<br>" +
                                "<b>Order:</b> %d</html>",
                        pokemon.getId(),
                        pokemon.getName(),
                        pokemon.getBaseExperience(),
                        pokemon.getHeight(),
                        pokemon.getWeight(),
                        pokemon.getOrderIndex()
                );
                infoLabel.setText(info);

                // Cargar imagen
                try {
                    URL spriteUrl = new URL(pokemon.getSprites().getFrontDefault());
                    ImageIcon spriteIcon = new ImageIcon(spriteUrl);
                    imageLabel.setIcon(new ImageIcon(spriteIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH)));
                } catch (Exception e) {
                    imageLabel.setText("Image not available");
                    imageLabel.setIcon(null);
                }

                // Cargar habilidades y movimientos
                abilityList.setListData(pokemon.getAbilities().stream()
                        .map(ability -> String.format("Name: %s, Slot: %d, Hidden: %s",
                                ability.getName(),
                                ability.getSlot(),
                                ability.isHidden() ? "Yes" : "No"))
                        .toArray(String[]::new));
                moveList.setListData(pokemon.getMoves().stream()
                        .map(Move::getName)
                        .toArray(String[]::new));
            } catch (Exception ex) {
                showError("An error occurred while displaying Pokémon data.");
            }
        });
    }

    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}

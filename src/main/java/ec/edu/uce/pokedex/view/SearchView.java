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
        this.panel = new JPanel(new BorderLayout(15, 15));
        this.panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Crear componentes principales
        JLabel titleLabel = ComponentFactory.createLabel("Search Pokémon", 35, SwingConstants.CENTER);
        this.searchField = createSearchField();
        JButton searchButton = createSearchButton(pokeService);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        this.imageLabel = ComponentFactory.createLabel("", 0, SwingConstants.CENTER);
        this.imageLabel.setPreferredSize(new Dimension(320, 320));

        this.abilityList = new JList<>();
        this.moveList = new JList<>();

        this.infoLabel = ComponentFactory.createLabel("Enter a Pokémon name to search", 16, SwingConstants.CENTER);
        this.infoLabel.setFont(uiConfig.labelFont());

        JPanel displayPanel = createDisplayPanel();

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(displayPanel, BorderLayout.SOUTH);
    }

    private JTextField createSearchField() {
        JTextField searchField = new JTextField(25);
        searchField.setFont(uiConfig.labelFont());
        searchField.setHorizontalAlignment(JTextField.CENTER);
        searchField.setBorder(BorderFactory.createLineBorder(uiConfig.primaryColor(), 2));
        return searchField;
    }

    private JButton createSearchButton(PokeService pokeService) {
        JButton searchButton = ComponentFactory.createButton("Search", 18, uiConfig.primaryColor(), uiConfig.secondaryColor());
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
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        displayPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel imageAndInfoPanel = new JPanel(new GridLayout(1, 2, 20, 0));

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createLineBorder(uiConfig.secondaryColor(), 2));
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(infoLabel, BorderLayout.CENTER);

        imageAndInfoPanel.add(imagePanel);
        imageAndInfoPanel.add(infoPanel);

        displayPanel.add(imageAndInfoPanel);

        JPanel abilitiesAndMovesPanel = new JPanel(new GridLayout(1, 2, 20, 0));

        JPanel abilityPanel = createListPanel("Abilities", abilityList);
        JPanel movePanel = createListPanel("Moves", moveList);

        abilitiesAndMovesPanel.add(abilityPanel);
        abilitiesAndMovesPanel.add(movePanel);

        displayPanel.add(Box.createVerticalStrut(25));
        displayPanel.add(abilitiesAndMovesPanel);

        return displayPanel;
    }

    private JPanel createListPanel(String title, JList<String> list) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titleLabel = ComponentFactory.createLabel(title, 22, SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(ComponentFactory.createScrollPane(list), BorderLayout.CENTER);
        return panel;
    }

    private void fetchAndDisplayPokemonInfo(PokeService pokeService, String name) {
        pokeService.getPokemonByName(name.toLowerCase())
                .ifPresentOrElse(this::displayPokemonInfo, () -> showError("Pokémon not found."));
    }

    private void displayPokemonInfo(Pokemon pokemon) {
        SwingUtilities.invokeLater(() -> {
            try {
                String info = String.format(
                        "<html><div style='text-align:center;'>\n" +
                                "    <h2 style='color:#2a9df4;'>Pokemon Found!</h2>" +
                                "<b>ID:</b> %d<br>" +
                                "<b>Name:</b> %s<br>" +
                                "<b>Base Experience:</b> %d<br>" +
                                "<b>Height:</b> %d<br>" +
                                "<b>Weight:</b> %d<br>" +
                                "<b>Order:</b> %d</div></html>",
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
                    imageLabel.setIcon(new ImageIcon(spriteIcon.getImage().getScaledInstance(320, 320, Image.SCALE_SMOOTH)));
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

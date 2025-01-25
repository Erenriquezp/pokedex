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
                showErrorMessage("Please enter a valid Pokémon name.");
                return;
            }
            fetchAndDisplayPokemonInfo(pokeService, name);
        });
        return searchButton;
    }

    private JPanel createDisplayPanel() {
        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.PAGE_AXIS));
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel para imagen e información (dividido en dos)
        JPanel imageAndInfoPanel = new JPanel(new GridLayout(1, 2, 10, 0)); // 1 fila, 2 columnas

        // Panel de imagen
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // Panel de información
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(infoLabel, BorderLayout.CENTER);

        // Añadir los paneles de imagen e información al contenedor dividido
        imageAndInfoPanel.add(imagePanel);
        imageAndInfoPanel.add(infoPanel);

        // Añadir el panel de imagen e información al display principal
        displayPanel.add(imageAndInfoPanel);

        // Panel de habilidades y movimientos (dividido en dos)
        JPanel abilitiesAndMovesPanel = new JPanel(new GridLayout(1, 2, 10, 0)); // 1 fila, 2 columnas

        // Panel de habilidades
        JPanel abilityPanel = new JPanel(new BorderLayout());
        JLabel abilityTitle = ComponentFactory.createLabel("Abilities", 16, SwingConstants.CENTER);
        abilityPanel.add(abilityTitle, BorderLayout.NORTH);

        JScrollPane abilityScrollPane = ComponentFactory.createScrollPane(abilityList);
        abilityScrollPane.setPreferredSize(new Dimension(300, 200)); // Tamaño ajustado
        abilityPanel.add(abilityScrollPane, BorderLayout.CENTER);

        // Panel de movimientos
        JPanel movePanel = new JPanel(new BorderLayout());
        JLabel moveTitle = ComponentFactory.createLabel("Moves", 16, SwingConstants.CENTER);
        movePanel.add(moveTitle, BorderLayout.NORTH);

        JScrollPane moveScrollPane = ComponentFactory.createScrollPane(moveList);
        moveScrollPane.setPreferredSize(new Dimension(300, 200)); // Tamaño ajustado
        movePanel.add(moveScrollPane, BorderLayout.CENTER);

        // Añadir los paneles de habilidades y movimientos al contenedor dividido
        abilitiesAndMovesPanel.add(abilityPanel);
        abilitiesAndMovesPanel.add(movePanel);

        // Añadir el panel dividido al display principal
        displayPanel.add(abilitiesAndMovesPanel);

        return displayPanel;
    }

    private void showErrorMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            infoLabel.setText(message);
            imageLabel.setIcon(null);
            abilityList.setListData(new String[0]);
            moveList.setListData(new String[0]);
            searchField.requestFocusInWindow();
        });
    }

    private void fetchAndDisplayPokemonInfo(PokeService pokeService, String name) {
        pokeService.getPokemonByName(name.toLowerCase())
                .ifPresentOrElse(this::displayPokemonInfo, () -> showErrorMessage("Pokémon not found."));
    }

    private void displayPokemonInfo(Pokemon pokemon) {
        SwingUtilities.invokeLater(() -> {
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

            try {
                URL spriteUrl = new URL(pokemon.getSprites().getFrontDefault());
                ImageIcon spriteIcon = new ImageIcon(spriteUrl);
                Image scaledImage = spriteIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            } catch (Exception e) {
                imageLabel.setText("Image not available");
                imageLabel.setIcon(null);
            }

            List<String> abilities = pokemon.getAbilities().stream()
                    .map(ability -> String.format("Name: %s, Slot: %d, Hidden: %s",
                            ability.getName(),
                            ability.getSlot(),
                            ability.isHidden() ? "Yes" : "No"))
                    .toList();
            abilityList.setListData(abilities.toArray(new String[0]));

            List<String> moves = pokemon.getMoves().stream()
                    .map(Move::getName)
                    .toList();
            moveList.setListData(moves.toArray(new String[0]));

            searchField.requestFocusInWindow();
        });
    }
}

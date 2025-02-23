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
import java.net.URI;

/**
 * Vista de búsqueda de Pokémon que permite al usuario buscar un Pokémon por su nombre
 * y mostrar la información correspondiente como su imagen, estadísticas, habilidades y movimientos.
 */
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

    /**
     * Constructor de la vista de búsqueda de Pokémon.
     *
     * @param pokeService Servicio que gestiona las consultas a la API de Pokémon.
     * @param uiConfig Configuración de la interfaz de usuario.
     */
    public SearchView(PokeService pokeService, UIConfig uiConfig) {
        this.uiConfig = uiConfig;
        this.panel = new JPanel(new BorderLayout(15, 15));
        this.panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Crear componentes principales
        JLabel titleLabel = ComponentFactory.createLabel("Search Pokémon", 35, SwingConstants.CENTER);
        titleLabel.setForeground(uiConfig.primaryColor()); // Cambiar color del título
        this.searchField = createSearchField();
        JButton searchButton = createSearchButton(pokeService);

        // Usar un panel con GridBagLayout para el panel de búsqueda
        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Espaciado entre componentes

        // Añadir campo de búsqueda
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // Permitir que el campo de búsqueda se expanda
        gbc.fill = GridBagConstraints.HORIZONTAL;
        searchPanel.add(searchField, gbc);

        // Añadir botón de búsqueda
        gbc.gridx = 1;
        gbc.weightx = 0; // No expandir el botón
        searchPanel.add(searchButton, gbc);

        // Establecer un borde y fondo para el panel de búsqueda
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Pokémon"));
        searchPanel.setBackground(Color.WHITE); // Fondo blanco para el panel de búsqueda

        this.imageLabel = ComponentFactory.createLabel("", 0, SwingConstants.CENTER);
        this.imageLabel.setPreferredSize(new Dimension(300, 300));
        this.abilityList = new JList<>();
        this.moveList = new JList<>();

        this.infoLabel = ComponentFactory.createLabel("Enter a Pokémon name to search", 16, SwingConstants.CENTER);
        this.infoLabel.setFont(uiConfig.labelFont());
        this.infoLabel.setForeground(uiConfig.secondaryColor()); // Cambiar color de la etiqueta de información

        JPanel displayPanel = createDisplayPanel();
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(displayPanel, BorderLayout.SOUTH);
    }

    /**
     * Crea el campo de texto donde el usuario ingresará el nombre del Pokémon a buscar.
     *
     * @return El campo de texto para la búsqueda.
     */
    private JTextField createSearchField() {
        JTextField searchField = new JTextField(25);
        searchField.setFont(uiConfig.labelFont());
        searchField.setHorizontalAlignment(JTextField.CENTER);
        searchField.setBorder(BorderFactory.createLineBorder(uiConfig.primaryColor(), 2));
        return searchField;
    }

    /**
     * Crea el botón de búsqueda y maneja su acción.
     * Al hacer clic, realiza la búsqueda del Pokémon y muestra su información.
     *
     * @param pokeService El servicio de consulta de Pokémon.
     * @return El botón de búsqueda.
     */
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

    /**
     * Crea el panel de visualización que contiene la imagen, la información,
     * las habilidades y los movimientos del Pokémon.
     *
     * @return El panel de visualización.
     */
    private JPanel createDisplayPanel() {
        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        displayPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel de imagen y info
        JPanel imageAndInfoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Imagen
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createLineBorder(uiConfig.secondaryColor(), 2));
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        imageAndInfoPanel.add(imagePanel, gbc);

        // Información
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        gbc.gridx = 1;
        imageAndInfoPanel.add(infoPanel, gbc);

        displayPanel.add(imageAndInfoPanel);

        // Panel de habilidades y movimientos
        JPanel abilitiesAndMovesPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        JPanel abilityPanel = createListPanel("Abilities", abilityList);
        JPanel movePanel = createListPanel("Moves", moveList);
        abilitiesAndMovesPanel.add(abilityPanel);
        abilitiesAndMovesPanel.add(movePanel);

        displayPanel.add(Box.createVerticalStrut(15));
        displayPanel.add(abilitiesAndMovesPanel);

        return displayPanel;
    }

    /**
     * Crea un panel con una lista y un título. Se utiliza para mostrar habilidades y movimientos.
     *
     * @param title El título de la lista (por ejemplo, "Abilities" o "Moves").
     * @param list La lista de elementos que se mostrarán en el panel.
     * @return El panel con la lista y su título.
     */
    private JPanel createListPanel(String title, JList<String> list) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titleLabel = ComponentFactory.createLabel(title, 20, SwingConstants.CENTER);
        titleLabel.setForeground(uiConfig.primaryColor()); // Cambiar el color del título

        panel.add(titleLabel, BorderLayout.NORTH);
        list.setFont(new Font("Arial", Font.BOLD, 14)); // Cambiar el tamaño de la fuente
        list.setForeground(Color.BLACK);
        panel.add(ComponentFactory.createScrollPane(list), BorderLayout.CENTER);
        list.setBorder(BorderFactory.createLineBorder(uiConfig.secondaryColor(), 2)); // Borde alrededor de la lista

        return panel;
    }

    /**
     * Realiza la búsqueda del Pokémon y, si lo encuentra, muestra su información.
     *
     * @param pokeService El servicio que maneja la búsqueda de Pokémon.
     * @param name El nombre del Pokémon a buscar.
     */
    private void fetchAndDisplayPokemonInfo(PokeService pokeService, String name) {
        pokeService.getPokemonByName(name.toLowerCase())
                .ifPresentOrElse(this::displayPokemonInfo, () -> showError("Pokémon not found."));
    }

    /**
     * Muestra la información de un Pokémon en la interfaz.
     * Actualiza la imagen, las estadísticas, habilidades y movimientos.
     *
     * @param pokemon El Pokémon cuyas información será mostrada.
     */
    private void displayPokemonInfo(Pokemon pokemon) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Limpiar el infoPanel antes de agregar nueva información
                infoLabel.removeAll(); // Limpiar el contenido anterior
                infoLabel.revalidate(); // Revalidar el panel
                infoLabel.repaint(); // Volver a dibujar el panel

                // Limpiar la imagen antes de cargar una nueva
                imageLabel.setIcon(null); // Limpiar la imagen
                imageLabel.setText("Loading..."); // Mensaje de carga

                // Crear un panel para mostrar la información del Pokémon
                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

                // Título
                JLabel titleLabel = new JLabel("Pokémon Found!", SwingConstants.CENTER);
                titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
                titleLabel.setForeground(uiConfig.primaryColor());
                infoPanel.add(titleLabel);

                // Información del Pokémon
                infoPanel.add(createInfoLabel("ID:", String.valueOf(pokemon.getId())));
                infoPanel.add(createInfoLabel("Name:", pokemon.getName()));
                infoPanel.add(createInfoLabel("Base Experience:", String.valueOf(pokemon.getBaseExperience())));
                infoPanel.add(createInfoLabel("Height:", String.valueOf(pokemon.getHeight())));
                infoPanel.add(createInfoLabel("Weight:", String.valueOf(pokemon.getWeight())));
                infoPanel.add(createInfoLabel("Order:", String.valueOf(pokemon.getOrderIndex())));

                // Agregar un separador
                infoPanel.add(new JSeparator()); // Separador

                // Agregar el panel de información al panel principal
                infoLabel.setLayout(new BorderLayout()); // Establecer el layout del infoLabel
                infoLabel.add(infoPanel, BorderLayout.CENTER); // Agregar el panel de información al infoLabel

                // Cargar imagen
                loadImage(pokemon.getSprites().getFrontDefault());

                // Cargar habilidades y movimientos
                loadAbilitiesAndMoves(pokemon);

                // Revalidar y repaint para asegurarse de que todo se actualice
                infoLabel.revalidate(); // Revalidar el infoLabel
                infoLabel.repaint(); // Volver a dibujar el infoLabel
            } catch (Exception ex) {
                showError("An error occurred while displaying Pokémon data.");
            }
        });
    }

    /**
     * Crea un panel con un título y un valor que muestra información adicional de un Pokémon.
     *
     * @param title El título de la información (por ejemplo, "ID:", "Name:", etc.).
     * @param value El valor correspondiente a esa información (por ejemplo, el nombre o la altura).
     * @return El panel con el título y el valor.
     */
    private JPanel createInfoLabel(String title, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(uiConfig.tertiaryColor()); // Color para el título

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        valueLabel.setForeground(Color.BLACK); // Color para el valor

        panel.add(titleLabel);
        panel.add(Box.createHorizontalStrut(8)); // Espacio horizontal entre título y valor
        panel.add(valueLabel);

        return panel;
    }

    /**
     * Carga la imagen del Pokémon a partir de su URL y la muestra en la interfaz.
     *
     * @param spriteUrl La URL de la imagen del Pokémon.
     */
    private void loadImage(String spriteUrl) {
        try {
            URI uri = new URI(spriteUrl);
            ImageIcon spriteIcon = new ImageIcon(uri.toURL());
            imageLabel.setIcon(new ImageIcon(spriteIcon.getImage().getScaledInstance(380, 380, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
            imageLabel.setText("Image not available");
            imageLabel.setIcon(null);
        }
    }

    /**
     * Carga y muestra las habilidades y movimientos del Pokémon en las listas correspondientes.
     *
     * @param pokemon El Pokémon cuyas habilidades y movimientos serán mostrados.
     */
    private void loadAbilitiesAndMoves(Pokemon pokemon) {
        abilityList.setListData(pokemon.getAbilities().stream()
                .map(ability -> String.format("Name: %s, Slot: %d, Hidden: %s",
                        ability.getName(),
                        ability.getSlot(),
                        ability.isHidden() ? "Yes" : "No"))
                .toArray(String[]::new));
        moveList.setListData(pokemon.getMoves().stream()
                .map(Move::getName)
                .toArray(String[]::new));
    }

    /**
     * Muestra un mensaje de error en caso de que ocurra algún problema durante la búsqueda
     * o la carga de datos.
     *
     * @param message El mensaje de error que se mostrará.
     */
    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}
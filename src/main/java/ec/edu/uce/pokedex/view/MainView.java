package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.config.UIConfig;
import ec.edu.uce.pokedex.service.PokeService;
import ec.edu.uce.pokedex.util.ComponentFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class MainView {

    private final UIConfig uiConfig;
    private final PokeService pokeService;
    private JFrame frame;

    public MainView(UIConfig uiConfig, PokeService pokeService) {
        this.uiConfig = uiConfig;
        this.pokeService = pokeService;
    }

    /**
     * Inicializa la vista principal.
     */
    public void initialize(
            SearchView searchView,
            SpriteView spriteView,
            HomeView examplePanel,
            StatView statView,
            TypeView typeView,
            EvolutionView evolutionView) {

        frame = new JFrame("Pokédex");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Configurar la ventana a pantalla completa
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(false);

        // Configurar panel principal
        JPanel mainPanel = createMainPanel(examplePanel, searchView, spriteView, statView, typeView, evolutionView);

        // Crear barra de menú
        JMenuBar menuBar = createMenuBar();
        frame.setJMenuBar(menuBar);

        // Crear panel de botones de navegación
        JPanel buttonPanel = createButtonPanel(mainPanel);

        // Configurar el frame
        frame.setLayout(new BorderLayout());
        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JPanel createMainPanel(
            HomeView examplePanel,
            SearchView searchView,
            SpriteView spriteView,
            StatView statView,
            TypeView typeView,
            EvolutionView evolutionView) {

        JPanel mainPanel = new JPanel(new CardLayout());
        mainPanel.add(examplePanel.getPanel(), "ExamplePanel");
        mainPanel.add(searchView.getPanel(), "SearchPanel");
        mainPanel.add(spriteView.getPanel(), "SpriteView");
        mainPanel.add(statView.getPanel(), "StatView");
        mainPanel.add(typeView.getPanel(), "TypeView");
        mainPanel.add(evolutionView.getPanel(), "EvolutionView");
        return mainPanel;
    }

    private JPanel createButtonPanel(JPanel mainPanel) {
        JPanel buttonPanel = new JPanel(new BorderLayout()); // Cambiar a BorderLayout
        buttonPanel.setBackground(uiConfig.secondaryColor());

        // Crear un panel para los botones de navegación
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        navigationPanel.setBackground(uiConfig.secondaryColor());

        navigationPanel.add(createNavigationButton("Home", mainPanel, "ExamplePanel"));
        navigationPanel.add(createNavigationButton("Search Pokémon", mainPanel, "SearchPanel"));
        navigationPanel.add(createNavigationButton("View Sprites", mainPanel, "SpriteView"));
        navigationPanel.add(createNavigationButton("View Stats", mainPanel, "StatView"));
        navigationPanel.add(createNavigationButton("View Pokémon by Type", mainPanel, "TypeView"));
        navigationPanel.add(createNavigationButton("View Evolution Chain", mainPanel, "EvolutionView"));

        // Botón para cargar Pokémon desde la API
        JButton loadApiButton = ComponentFactory.createButton("Load Data from API", 16, Color.RED, Color.WHITE); // Cambia los colores según sea necesario
        loadApiButton.addActionListener(createLoadApiAction());

        // Añadir el panel de navegación y el botón de carga al panel principal
        buttonPanel.add(navigationPanel, BorderLayout.CENTER); // Panel de navegación en el centro
        buttonPanel.add(loadApiButton, BorderLayout.EAST); // Botón de carga a la derecha

        return buttonPanel;
    }

    private JButton createNavigationButton(String text, JPanel mainPanel, String cardName) {
        JButton button = ComponentFactory.createButton(text, 16, uiConfig.primaryColor(), uiConfig.secondaryColor());
        button.addActionListener(e -> {
            CardLayout cl = (CardLayout) mainPanel.getLayout();
            cl.show(mainPanel, cardName);
        });
        return button;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showMessage("Pokédex Application\nCreated using Spring and Java Swing."));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private ActionListener createLoadApiAction() {
        return (ActionEvent e) -> {
            int limit = 10; // Configura el número máximo de Pokémon a cargar
            int offset = 0; // Configura el punto de inicio

            try {
                System.out.println("Iniciando carga de datos desde la API...");
                pokeService.loadAllPokemonsFromApiAndSave(limit, offset);
                System.out.println("Carga de datos completada exitosamente.");
            } catch (Exception ex) {
                System.err.println("Error al cargar datos desde la API: " + ex.getMessage());
                ex.printStackTrace(); // Imprime el seguimiento completo del error
            }
        };
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

}

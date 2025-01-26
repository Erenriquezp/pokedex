package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.config.UIConfig;
import ec.edu.uce.pokedex.service.PokeService;
import ec.edu.uce.pokedex.util.ComponentFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

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
            HomeView homeView,
            StatView statView,
            TypeView typeView,
            EvolutionView evolutionView) {

        frame = new JFrame("Pokédex");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(false);

        // Configurar panel principal
        JPanel mainPanel = createMainPanel(homeView, searchView, spriteView, statView, typeView, evolutionView);

        // Crear barra de menú
        JMenuBar menuBar = createMenuBar();
        frame.setJMenuBar(menuBar);

        // Crear panel de botones de navegación
        JPanel buttonPanel = createButtonPanel(mainPanel, homeView);

        frame.setLayout(new BorderLayout());
        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);

        // Verificar si hay datos en la base de datos
        if (pokeService.getPokemonByName("ditto").isPresent()) {
            ((CardLayout) mainPanel.getLayout()).show(mainPanel, "HomeView");
        } else {
            showInitialMessage(mainPanel);
        }

        frame.setVisible(true);
    }

    private JPanel createMainPanel(
            HomeView homeView,
            SearchView searchView,
            SpriteView spriteView,
            StatView statView,
            TypeView typeView,
            EvolutionView evolutionView) {

        JPanel mainPanel = new JPanel(new CardLayout());
        mainPanel.add(homeView.getPanel(), "HomeView");
        mainPanel.add(searchView.getPanel(), "SearchView");
        mainPanel.add(spriteView.getPanel(), "SpriteView");
        mainPanel.add(statView.getPanel(), "StatView");
        mainPanel.add(evolutionView.getPanel(), "EvolutionView");
        mainPanel.add(typeView.getPanel(), "TypeView");
        return mainPanel;
    }

    private JPanel createButtonPanel(JPanel mainPanel, HomeView homeView) {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(uiConfig.secondaryColor());

        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        navigationPanel.setBackground(uiConfig.secondaryColor());

        navigationPanel.add(createNavigationButton("Home", mainPanel, "HomeView"));
        navigationPanel.add(createNavigationButton("Search Pokémon", mainPanel, "SearchView"));
        navigationPanel.add(createNavigationButton("View Sprites", mainPanel, "SpriteView"));
        navigationPanel.add(createNavigationButton("View Stats", mainPanel, "StatView"));
        navigationPanel.add(createNavigationButton("View Evolution Chain", mainPanel, "EvolutionView"));
        navigationPanel.add(createNavigationButton("View Pokémon by Type", mainPanel, "TypeView"));

        JButton loadApiButton = ComponentFactory.createButton("Load Data from API", 16, Color.RED, Color.WHITE);
        loadApiButton.addActionListener(createLoadApiAction(mainPanel, homeView));

        buttonPanel.add(navigationPanel, BorderLayout.CENTER);
        buttonPanel.add(loadApiButton, BorderLayout.EAST);

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

    private ActionListener createLoadApiAction(JPanel mainPanel, HomeView homeView) {
        return e -> {
            JDialog progressDialog = createProgressDialog();
            JProgressBar progressBar = (JProgressBar) progressDialog.getContentPane().getComponent(0);

            SwingWorker<Void, Integer> worker = new SwingWorker<>() {
                private int progress = 0;

                @Override
                protected Void doInBackground() {
                    int offset = 0;

                    // Número total de Pokémon a cargar
                    int totalPokemons = 1025;
                    for (int i = 0; i < totalPokemons; i++) {
                        try {
                            pokeService.loadAllPokemonsFromApiAndSave(1, offset++);
                            progress++;
                            publish(progress); // Publicar el progreso actual
                        } catch (Exception ex) {
                            System.err.println("Error loading Pokémon: " + ex.getMessage());
                        }
                    }
                    return null;
                }

                @Override
                protected void process(List<Integer> chunks) {
                    int latestProgress = chunks.getLast();
                    progressBar.setValue(latestProgress); // Actualizar el valor de la barra de progreso
                }

                @Override
                protected void done() {
                    progressDialog.dispose();
                    showMessage("Pokémon data loaded successfully!");

                    // Precargar la vista Home en un nuevo hilo
                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() {
                            homeView.refreshView(); // Preparar los datos de la vista Home
                            return null;
                        }

                        @Override
                        protected void done() {
                            SwingUtilities.invokeLater(() -> {
                                ((CardLayout) mainPanel.getLayout()).show(mainPanel, "HomeView");
                            });
                        }
                    }.execute();
                }
            };

            progressBar.setMaximum(1025); // Establecer el máximo de la barra de progreso
            worker.execute();
            progressDialog.setVisible(true);
        };
    }

    private JDialog createProgressDialog() {
        JDialog dialog = new JDialog(frame, "Loading Pokémon", true);
        JProgressBar progressBar = new JProgressBar(0, 1000);
        progressBar.setStringPainted(true);
        dialog.getContentPane().add(progressBar, BorderLayout.CENTER);
        dialog.setSize(300, 100);
        dialog.setLocationRelativeTo(frame);
        return dialog;
    }

    private void showInitialMessage(JPanel mainPanel) {
        JPanel messagePanel = new JPanel(new BorderLayout());
        JLabel messageLabel = ComponentFactory.createLabel("No data found. Please use the 'Load Data from API' button.", 20, SwingConstants.CENTER);
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        mainPanel.add(messagePanel, "NoDataView");
        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "NoDataView");
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}

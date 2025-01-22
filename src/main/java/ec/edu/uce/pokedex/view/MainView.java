package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.config.UIConfig;
import ec.edu.uce.pokedex.util.ComponentFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class MainView {

    private final UIConfig uiConfig;

    public MainView(UIConfig uiConfig) {
        this.uiConfig = uiConfig;
    }

    private JFrame frame;

    public void initialize(SearchPanel searchPanel, AbilityView abilityView, SpriteView spriteView, ExamplePanel examplePanel, StatView statView, TypeView typeView, MoveView moveView, EvolutionView evolutionView) {
        frame = new JFrame("Pokédex");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Configurar la ventana a pantalla completa
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(false);

        // Configurar panel principal
        JPanel mainPanel = createMainPanel(examplePanel, searchPanel, abilityView, spriteView, statView, typeView, moveView, evolutionView);

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

    private JPanel createMainPanel(ExamplePanel examplePanel, SearchPanel searchPanel, AbilityView abilityView, SpriteView spriteView, StatView statView, TypeView typeView, MoveView moveView, EvolutionView evolutionView) {
        JPanel mainPanel = new JPanel(new CardLayout());
        mainPanel.add(examplePanel.getPanel(), "ExamplePanel");
        mainPanel.add(searchPanel.getPanel(), "SearchPanel");
        mainPanel.add(abilityView.getPanel(), "AbilityView");
        mainPanel.add(spriteView.getPanel(), "SpriteView");
        mainPanel.add(statView.getPanel(), "StatView");
        mainPanel.add(typeView.getPanel(), "TypeView");
        mainPanel.add(moveView.getPanel(), "MoveView");
        mainPanel.add(evolutionView.getPanel(), "EvolutionView");
        return mainPanel;
    }

    private JPanel createButtonPanel(JPanel mainPanel) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(uiConfig.secondaryColor());

        buttonPanel.add(createNavigationButton("Home", mainPanel, "ExamplePanel"));
        buttonPanel.add(createNavigationButton("Search Pokémon", mainPanel, "SearchPanel"));
        buttonPanel.add(createNavigationButton("View Abilities", mainPanel, "AbilityView"));
        buttonPanel.add(createNavigationButton("View Sprites", mainPanel, "SpriteView"));
        buttonPanel.add(createNavigationButton("View Stats", mainPanel, "StatView"));
        buttonPanel.add(createNavigationButton("View Pokémon by Type", mainPanel, "TypeView"));
        buttonPanel.add(createNavigationButton("View Moves", mainPanel, "MoveView"));
        buttonPanel.add(createNavigationButton("View Evolution Chain", mainPanel, "EvolutionView"));

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
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(frame,
                "Pokédex Application\nCreated using Spring and Java Swing.",
                "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }
}

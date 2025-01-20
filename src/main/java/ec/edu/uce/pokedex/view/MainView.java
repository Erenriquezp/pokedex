package ec.edu.uce.pokedex.view;

import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class MainView {

    private JFrame frame;

    public void initialize(SearchPanel searchPanel, AbilityView abilityView, SpriteView spriteView, ExamplePanel examplePanel, StatView statView, TypeView typeView, MoveView moveView, EvolutionView evolutionView) {
        frame = new JFrame("Pokédex");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 960);
        frame.setLayout(new BorderLayout());

        // Crear barra de menú
        JMenuBar menuBar = createMenuBar();
        frame.setJMenuBar(menuBar);

        // Configurar panel principal
        JPanel mainPanel = new JPanel(new CardLayout());
        mainPanel.add(examplePanel.getPanel(), "ExamplePanel");
        mainPanel.add(searchPanel.getPanel(), "SearchPanel");
        mainPanel.add(abilityView.getPanel(), "AbilityView");
        mainPanel.add(spriteView.getPanel(), "SpriteView");
        mainPanel.add(statView.getPanel(), "StatView");
        mainPanel.add(typeView.getPanel(), "TypeView");
        mainPanel.add(moveView.getPanel(), "MoveView");
        mainPanel.add(evolutionView.getPanel(), "EvolutionView"); // Agregar EvolutionView al mainPanel

        JPanel buttonPanel = createButtonPanel(mainPanel);

        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private static JPanel createButtonPanel(JPanel mainPanel) {
        JPanel buttonPanel = new JPanel();
        JButton homeButton = new JButton("Home");
        JButton searchButton = new JButton("Search Pokémon");
        JButton abilitiesButton = new JButton("View Abilities");
        JButton spritesButton = new JButton("View Sprites");
        JButton statsButton = new JButton("View Stats");
        JButton typesButton = new JButton("View Pokémon by Type");
        JButton movesButton = new JButton("View Moves");
        JButton evolutionButton = new JButton("View Evolution Chain"); // Botón para EvolutionView

        homeButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) mainPanel.getLayout();
            cl.show(mainPanel, "ExamplePanel");
        });

        searchButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) mainPanel.getLayout();
            cl.show(mainPanel, "SearchPanel");
        });

        abilitiesButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) mainPanel.getLayout();
            cl.show(mainPanel, "AbilityView");
        });

        spritesButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) mainPanel.getLayout();
            cl.show(mainPanel, "SpriteView");
        });

        statsButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) mainPanel.getLayout();
            cl.show(mainPanel, "StatView");
        });

        typesButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) mainPanel.getLayout();
            cl.show(mainPanel, "TypeView");
        });

        movesButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) mainPanel.getLayout();
            cl.show(mainPanel, "MoveView");
        });

        evolutionButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) mainPanel.getLayout();
            cl.show(mainPanel, "EvolutionView");
        });

        buttonPanel.add(homeButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(abilitiesButton);
        buttonPanel.add(spritesButton);
        buttonPanel.add(statsButton);
        buttonPanel.add(typesButton);
        buttonPanel.add(movesButton);
        buttonPanel.add(evolutionButton); // Agregar botón al panel
        return buttonPanel;
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

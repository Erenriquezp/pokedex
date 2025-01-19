package ec.edu.uce.pokedex.view;

import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class MainView {

    private JFrame frame;

    public void initialize(SearchPanel searchPanel, AbilityView abilityView, SpriteView spriteView, ExamplePanel examplePanel) {
        frame = new JFrame("Pokédex");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
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

        JPanel buttonPanel = new JPanel();
        JButton homeButton = new JButton("Home");
        JButton searchButton = new JButton("Search Pokémon");
        JButton abilitiesButton = new JButton("View Abilities");
        JButton spritesButton = new JButton("View Sprites");

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

        buttonPanel.add(homeButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(abilitiesButton);
        buttonPanel.add(spritesButton);

        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);

        frame.setVisible(true);
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

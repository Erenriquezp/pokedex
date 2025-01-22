package ec.edu.uce.pokedex.util;

import javax.swing.*;
import java.awt.*;

public class ComponentFactory {

    public static JButton createButton(String text, int fontSize, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, fontSize));
        button.setBackground(background);
        button.setForeground(foreground);
        return button;
    }

    public static JLabel createLabel(String text, int fontSize, int alignment) {
        JLabel label = new JLabel(text, alignment);
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
        return label;
    }

    public static JPanel createSearchPanel(JTextField searchField, JButton searchButton) {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        return searchPanel;
    }
}

package ec.edu.uce.pokedex.util;

import javax.swing.*;
import java.awt.*;

public class ComponentFactory {

    /**
     * Creates a button with the specified properties.
     *
     * @param text        Text to display on the button.
     * @param fontSize    Font size of the button text.
     * @param background  Background color of the button.
     * @param foreground  Foreground (text) color of the button.
     * @return Configured JButton instance.
     */
    public static JButton createButton(String text, int fontSize, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, fontSize));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(4, 10, 5, 10));
        return button;
    }

    /**
     * Creates a label with the specified properties.
     *
     * @param text      Text to display on the label.
     * @param fontSize  Font size of the label text.
     * @param alignment Alignment of the label text (e.g., SwingConstants.CENTER).
     * @return Configured JLabel instance.
     */
    public static JLabel createLabel(String text, int fontSize, int alignment) {
        JLabel label = new JLabel(text, alignment);
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
        return label;
    }

    /**
     * Creates a search panel with a text field and a button.
     *
     * @param searchField Text field for input.
     * @param searchButton Button to trigger the search action.
     * @return Configured JPanel instance.
     */
    public static JPanel createSearchPanel(JTextField searchField, JButton searchButton) {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        return searchPanel;
    }

    /**
     * Creates a text field with the specified properties.
     *
     * @param columns  Number of columns for the text field.
     * @param alignment Alignment of the text in the field (e.g., JTextField.CENTER).
     * @return Configured JTextField instance.
     */
    public static JTextField createTextField(int columns, int alignment) {
        JTextField textField = new JTextField(columns);
        textField.setHorizontalAlignment(alignment);
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        return textField;
    }

    /**
     * Creates a scroll pane for a given component.
     *
     * @param component The component to add to the scroll pane.
     * @return Configured JScrollPane instance.
     */
    public static JScrollPane createScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    /**
     * Creates a styled JPanel.
     *
     * @param layout Layout manager for the panel.
     * @param backgroundColor Background color of the panel.
     * @return Configured JPanel instance.
     */
    public static JPanel createPanel(LayoutManager layout, Color backgroundColor) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(backgroundColor);
        return panel;
    }
}

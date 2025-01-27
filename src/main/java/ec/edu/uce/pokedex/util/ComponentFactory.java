package ec.edu.uce.pokedex.util;

import javax.swing.*;
import java.awt.*;

public class ComponentFactory {

    /**
     * Crea un botón con las propiedades especificadas.
     *
     * @param text Texto que se mostrará en el botón.
     * @param fontSize Tamaño de fuente del texto del botón.
     * @param background Color de fondo del botón.
     * @param foreground Color de primer plano (texto) del botón.
     * @return Configured de JButton configurada.
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
     * Crea una etiqueta con las propiedades especificadas.
     *
     * @param text Texto que se mostrará en la etiqueta.
     * @param fontSize Tamaño de fuente del texto de la etiqueta.
     * @param alignment Alineación del texto de la etiqueta (p. ej., SwingConstants. CENTER).
     * @return Configured de JLabel configurada.
     */
    public static JLabel createLabel(String text, int fontSize, int alignment) {
        JLabel label = new JLabel(text, alignment);
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
        return label;
    }

    /**
     * Crea un panel de búsqueda con un campo de texto y un botón.
     *
     * @param searchField Campo de texto para entrada.
     * @param searchButton Botón para activar la acción de búsqueda.
     * @return Configured de JPanel configurada.
     */
    public static JPanel createSearchPanel(JTextField searchField, JButton searchButton) {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        return searchPanel;
    }

    /**
     * Crea un campo de texto con las propiedades especificadas.
     *
     * @param columns Número de columnas para el campo de texto.
     * @param alignment Alineación del texto en el campo (p. ej., JTextField. CENTER).
     * @return Configured de JTextField configurada.
     */
    public static JTextField createTextField(int columns, int alignment) {
        JTextField textField = new JTextField(columns);
        textField.setHorizontalAlignment(alignment);
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        return textField;
    }

    /**
     * Crea un panel de desplazamiento para un componente determinado.
     *
     * @param component El componente que se agregará al panel de desplazamiento.
     * @return Configured JScrollPane configurada.
     */
    public static JScrollPane createScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    /**
     * Crea un JPanel con estilo.
     *
     * @param layout Administrador de diseño para el panel.
     * @param backgroundColor Color de fondo del panel.
     * @return Configured de JPanel configurada.
     */
    public static JPanel createPanel(LayoutManager layout, Color backgroundColor) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(backgroundColor);
        return panel;
    }
}

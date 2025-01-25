package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.models.Pokemon;
import ec.edu.uce.pokedex.util.ComponentFactory;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

@Getter
@Component
public class OptionButtonPanel {

    private final JPanel panel;

    public OptionButtonPanel() {
        this.panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setVisible(false); // Inicialmente oculto
    }

    public void updateButtons(Pokemon pokemon, BiConsumer<String, Pokemon> navigateToView) {
        panel.removeAll();
        addButton("View Abilities", "AbilityView", navigateToView, pokemon);
        addButton("View Stats", "StatView", navigateToView, pokemon);
        addButton("View Moves", "MoveView", navigateToView, pokemon);
        addButton("View Sprites", "SpriteView", navigateToView, pokemon);
        addButton("Evolution Chain", "EvolutionView", navigateToView, pokemon);

        panel.setVisible(true);
        panel.revalidate();
        panel.repaint();
    }

    private void addButton(String text, String targetView, BiConsumer<String, Pokemon> navigateToView, Pokemon pokemon) {
        JButton button = ComponentFactory.createButton(text, 14, Color.DARK_GRAY, Color.WHITE);
        button.addActionListener(e -> navigateToView.accept(targetView, pokemon));
        panel.add(button);
    }

    public void setVisible(boolean visible) {
        panel.setVisible(visible);
    }
}
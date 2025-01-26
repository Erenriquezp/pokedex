package ec.edu.uce.pokedex.config;

import org.springframework.context.annotation.Configuration;

import java.awt.*;

@Configuration
public class UIConfig {

    public Font titleFont() {
        return new Font("Arial", Font.BOLD, 24);
    }

    public Font labelFont() {
        return new Font("Arial", Font.PLAIN, 16);
    }

    public Color primaryColor() {
        return Color.BLUE;
    }

    public Color secondaryColor() {
        return Color.WHITE;
    }

    public Color tertiaryColor() {return Color.BLACK;}
}

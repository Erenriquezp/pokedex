package ec.edu.uce.pokedex;

import ec.edu.uce.pokedex.controller.PokedexController;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.builder.SpringApplicationBuilder;

import javax.swing.*;

@SpringBootApplication
public class PokedexApplication implements CommandLineRunner {
    private final PokedexController controller;

    public PokedexApplication(PokedexController controller) {
        this.controller = controller;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(PokedexApplication.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        javax.swing.SwingUtilities.invokeLater(controller::startGUI);

    }
}


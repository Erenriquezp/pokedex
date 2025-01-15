package ec.edu.uce.pokedex;

import ec.edu.uce.pokedex.view.MainView;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;

import javax.swing.*;

@SpringBootApplication
public class PokedexApplication implements CommandLineRunner {

    private final MainView mainView;

    public PokedexApplication(MainView mainView) {
        this.mainView = mainView;
    }

    public static void main(String[] args) {
// Asegúrate de que se ejecute en un entorno gráfico
        SwingUtilities.invokeLater(() -> {
            SpringApplication.run(PokedexApplication.class, args);
            new MainView(); // Asegúrate de que MainView sea tu clase JFrame
        });    }

    @Override
    public void run(String... args) {
        // Ejecutar la GUI de Swing en el hilo EDT
        javax.swing.SwingUtilities.invokeLater(mainView::initialize);
    }
}


package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.model.Pokemon;
import ec.edu.uce.pokedex.service.PokeService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutionException;

@Component
public class SearchPanel extends JPanel {

    private final PokeService pokeService;
    private JTextField searchField;
    private JButton searchButton;
    private JLabel resultLabel;

    public SearchPanel(PokeService pokeService) {
        this.pokeService = pokeService;
        initialize();
    }

    private void initialize() {
        this.setLayout(new FlowLayout());

        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        resultLabel = new JLabel("Results will appear here");

        searchButton.addActionListener(e -> performSearch());

        this.add(searchField);
        this.add(searchButton);
        this.add(resultLabel);
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            resultLabel.setText("Please enter a Pokémon name or ID.");
            return;
        }

        // Usamos un SwingWorker para ejecutar la solicitud de búsqueda en un hilo separado
        new SwingWorker<Pokemon, Void>() {
            @Override
            protected Pokemon doInBackground() throws Exception {
                return pokeService.getPokemonByName(query).block(); // Ejecutamos la solicitud sin bloquear la UI
            }

            @Override
            protected void done() {
                try {
                    Pokemon pokemon = get();
                    resultLabel.setText("Found: " + pokemon.getName());
                } catch (InterruptedException | ExecutionException e) {
                    resultLabel.setText("Error: Pokémon not found");
                }
            }
        }.execute();
    }
}

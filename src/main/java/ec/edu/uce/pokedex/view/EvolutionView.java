package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.controller.EvolutionController;
import lombok.Getter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Component
public class EvolutionView {

    @Getter
    private final JPanel panel;
    private final EvolutionController controller;

    public EvolutionView(EvolutionController controller) {
        this.controller = controller;
        this.panel = new JPanel(new BorderLayout());
        initialize();
    }

    private void initialize() {
        JLabel titleLabel = new JLabel("Pokémon Evolution Chain Viewer", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel evolutionPanel = new JPanel();
        evolutionPanel.setLayout(new BoxLayout(evolutionPanel, BoxLayout.X_AXIS));
        evolutionPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(evolutionPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> {
            String speciesName = searchField.getText().trim();
            if (speciesName.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter a species name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            fetchAndDisplayEvolutionChain(speciesName, evolutionPanel);
        });
    }

    private void fetchAndDisplayEvolutionChain(String speciesName, JPanel evolutionPanel) {
        controller.getEvolutionChain(speciesName)
                .doOnNext(chain -> SwingUtilities.invokeLater(() -> populateEvolutionPanel(chain, evolutionPanel)))
                .doOnError(err -> SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(panel, "Error: Unable to fetch evolution chain.", "Error", JOptionPane.ERROR_MESSAGE)
                ))
                .subscribe();
    }

    private void populateEvolutionPanel(List<Map<String, Object>> chain, JPanel evolutionPanel) {
        evolutionPanel.removeAll();

        for (Map<String, Object> stage : chain) {
            JPanel stagePanel = new JPanel(new BorderLayout());
            stagePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
            stagePanel.setBackground(Color.WHITE);

            Map<String, Object> species = (Map<String, Object>) stage.get("species");
            String speciesName = (String) species.get("name");
            String imageUrl = String.format("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/%s.png", extractIdFromUrl((String) species.get("url")));

            JLabel nameLabel = new JLabel(speciesName, SwingConstants.CENTER);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JLabel spriteLabel = new JLabel();
            spriteLabel.setHorizontalAlignment(SwingConstants.CENTER);
            try {
                ImageIcon spriteIcon = new ImageIcon(new URL(imageUrl));
                spriteLabel.setIcon(new ImageIcon(spriteIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
            } catch (Exception e) {
                spriteLabel.setText("Image not available");
            }

            stagePanel.add(nameLabel, BorderLayout.NORTH);
            stagePanel.add(spriteLabel, BorderLayout.CENTER);
            evolutionPanel.add(stagePanel);
        }

        evolutionPanel.revalidate();
        evolutionPanel.repaint();
    }

    private String extractIdFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }
}

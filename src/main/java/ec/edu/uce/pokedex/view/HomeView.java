package ec.edu.uce.pokedex.view;

import ec.edu.uce.pokedex.config.UIConfig;
import ec.edu.uce.pokedex.dto.PokemonDto;
import ec.edu.uce.pokedex.models.Ability;
import ec.edu.uce.pokedex.models.Type;
import ec.edu.uce.pokedex.service.PokeServiceDto;
import ec.edu.uce.pokedex.util.ComponentFactory;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Vista principal de la aplicación Pokédex que maneja la visualización de los Pokémon
 * y su paginación en la interfaz gráfica.
 */
@Component
public class HomeView {

    @Getter
    private final JPanel panel;
    private final PokeServiceDto pokeServiceDto;
    private final UIConfig uiConfig;
    private int offset = 0; // Controla la posición actual de paginación
    private static final int POKEMON_PER_PAGE = 6;

    /**
     * Constructor de la vista principal.
     *
     * @param pokeServiceDto Servicio para obtener datos de los Pokémon.
     * @param uiConfig Configuración de la interfaz de usuario.
     */
    public HomeView(PokeServiceDto pokeServiceDto, UIConfig uiConfig) {
        this.pokeServiceDto = pokeServiceDto;
        this.uiConfig = uiConfig;
        this.panel = new JPanel(new BorderLayout(0, 0)); // Elimina el espaciado en el BorderLayout
        initialize();
    }

    /**
     * Inicializa los componentes de la vista, como los paneles, botones y etiquetas.
     * Configura la paginación y carga la primera página de Pokémon.
     */
    private void initialize() {
        JLabel titleLabel = ComponentFactory.createLabel("Pokédex - Home", 35, SwingConstants.CENTER);
        JPanel pokemonPanel = new JPanel(new GridLayout(2, 3, 10, 10)); // Asegura que las tarjetas ocupen todo el espacio
        JScrollPane scrollPane = new JScrollPane(pokemonPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Quitar borde del JScrollPane
        scrollPane.setPreferredSize(new Dimension(0, 0)); // Esto debería hacer que ocupe toda la ventana disponible

        JButton backButton = createNavigationButton("Back", e -> navigate(-1, pokemonPanel));
        JButton nextButton = createNavigationButton("Next", e -> navigate(1, pokemonPanel));

        JPanel navigationPanel = ComponentFactory.createPanel(new FlowLayout(FlowLayout.CENTER), uiConfig.secondaryColor());
        navigationPanel.add(backButton);
        navigationPanel.add(nextButton);

        panel.setBackground(uiConfig.secondaryColor());
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(navigationPanel, BorderLayout.SOUTH);

        loadPokemonPage(pokemonPanel);
    }

    /**
     * Crea un botón de navegación con el texto especificado y la acción asociada.
     *
     * @param text Texto que se mostrará en el botón.
     * @param actionListener Acción que se ejecutará al hacer clic en el botón.
     * @return El botón creado.
     */
    private JButton createNavigationButton(String text, ActionListener actionListener) {
        JButton button = ComponentFactory.createButton(text, 18, uiConfig.primaryColor(), uiConfig.secondaryColor());
        button.addActionListener(actionListener);
        return button;
    }

    /**
     * Maneja la navegación entre páginas de Pokémon.
     *
     * @param direction Dirección de la navegación, positivo para siguiente página y negativo para la anterior.
     * @param pokemonPanel El panel donde se muestran los Pokémon.
     */
    private void navigate(int direction, JPanel pokemonPanel) {
        if (direction < 0 && offset > 0) {
            offset -= POKEMON_PER_PAGE;
        } else if (direction > 0) {
            offset += POKEMON_PER_PAGE;
        } else {
            showError("You're already on the first page.");
            return;
        }
        loadPokemonPage(pokemonPanel);
    }

    /**
     * Carga una página de Pokémon, actualizando el panel de Pokémon en la vista.
     *
     * @param pokemonPanel El panel donde se mostrarán las tarjetas de Pokémon.
     */
    private void loadPokemonPage(JPanel pokemonPanel) {
        try {
            List<PokemonDto> pokemonDtos = pokeServiceDto.getPokemonPage(offset, POKEMON_PER_PAGE);
            pokemonPanel.removeAll();
            pokemonDtos.forEach(dto -> pokemonPanel.add(createPokemonCard(dto)));
            pokemonPanel.revalidate();
            pokemonPanel.repaint();
        } catch (Exception e) {
            showError("Failed to load Pokémon data: " + e.getMessage());
        }
    }

    /**
     * Crea una tarjeta de información para un Pokémon específico.
     *
     * @param dto El objeto que contiene los datos del Pokémon.
     * @return El panel con la tarjeta de Pokémon.
     */
    private JPanel createPokemonCard(PokemonDto dto) {
        JPanel card = ComponentFactory.createPanel(new BorderLayout(0, 0), uiConfig.secondaryColor());
        card.setBorder(BorderFactory.createLineBorder(uiConfig.tertiaryColor(), 2));

        JLabel nameLabel = ComponentFactory.createLabel(dto.getName().toUpperCase(), 25, SwingConstants.CENTER);
        JLabel spriteLabel = new JLabel();
        spriteLabel.setHorizontalAlignment(SwingConstants.CENTER);

        loadSpriteAsync(dto.getSpriteUrl(), spriteLabel, card);

        JPanel detailsPanel = createDetailsPanel(dto);
        card.add(nameLabel, BorderLayout.NORTH);
        card.add(spriteLabel, BorderLayout.CENTER);
        card.add(detailsPanel, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Carga de manera asíncrona el sprite del Pokémon.
     *
     * @param spriteUrl La URL del sprite.
     * @param spriteLabel El componente JLabel donde se mostrará la imagen.
     * @param card El panel donde se encuentra el JLabel.
     */
    private void loadSpriteAsync(String spriteUrl, JLabel spriteLabel, JPanel card) {
        CompletableFuture.supplyAsync(() -> {
            try {
                URI uri = new URI(spriteUrl);
                ImageIcon spriteIcon = new ImageIcon(uri.toURL());
                Image scaledImage = spriteIcon.getImage().getScaledInstance(190, 190, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            } catch (Exception e) {
                return new ImageIcon("Failed to load image");
            }
        }).thenAcceptAsync(spriteIcon -> {
            spriteLabel.setIcon(spriteIcon);
            card.revalidate();
            card.repaint();
        }, SwingUtilities::invokeLater);
    }

    /**
     * Crea un panel con los detalles del Pokémon, como los tipos y habilidades.
     *
     * @param dto El objeto que contiene los datos del Pokémon.
     * @return El panel con los detalles del Pokémon.
     */
    private JPanel createDetailsPanel(PokemonDto dto) {
        // Usamos un GridLayout con 1 fila y 2 columnas
        JPanel detailsPanel = ComponentFactory.createPanel(new GridLayout(1, 2, 0, 10), uiConfig.secondaryColor());

        // Panel izquierdo para los tipos
        JPanel typePanel = createTypePanel(dto.getTypes());
        // Panel derecho para las habilidades
        JPanel abilityPanel = createAbilityPanel(dto.getAbilities());

        detailsPanel.add(typePanel);    // Agregar tipos al panel izquierdo
        detailsPanel.add(abilityPanel); // Agregar habilidades al panel derecho

        return detailsPanel;
    }

    /**
     * Crea un panel con la lista de tipos del Pokémon.
     *
     * @param types Lista de tipos del Pokémon.
     * @return El panel con los tipos del Pokémon.
     */
    private JPanel createTypePanel(List<Type> types) {
        return createInfoPanel("Types: ", types.stream().map(Type::getName).distinct().toList());
    }

    /**
     * Crea un panel con la lista de habilidades del Pokémon.
     *
     * @param abilities Lista de habilidades del Pokémon.
     * @return El panel con las habilidades del Pokémon.
     */
    private JPanel createAbilityPanel(List<Ability> abilities) {
        return createInfoPanel("Abilities: ", abilities.stream().map(Ability::getName).distinct().limit(2).toList());
    }

    /**
     * Crea un panel con un título y una lista de elementos.
     *
     * @param title El título que aparecerá en la parte superior del panel.
     * @param items La lista de elementos que se mostrarán en el panel.
     * @return El panel con el título y los elementos.
     */
    private JPanel createInfoPanel(String title, List<String> items) {
        JLabel titleLabel = ComponentFactory.createLabel(title, 16, SwingConstants.LEFT);
        titleLabel.setForeground(uiConfig.primaryColor());
        JLabel contentLabel = ComponentFactory.createLabel(String.join(", ", items), 14, SwingConstants.LEFT);

        JPanel panel = ComponentFactory.createPanel(new BorderLayout(), uiConfig.secondaryColor());
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(contentLabel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Actualiza la vista de la Pokédex, cargando nuevamente la primera página.
     */
    public void refreshView() {
        offset = 0; // Reiniciar el paginador al inicio
        JPanel pokemonPanel = (JPanel) ((JScrollPane) panel.getComponent(1)).getViewport().getView();
        loadPokemonPage(pokemonPanel);
    }

    /**
     * Muestra un mensaje de error en la vista principal.
     *
     * @param message El mensaje de error a mostrar.
     */
    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}
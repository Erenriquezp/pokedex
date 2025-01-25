package ec.edu.uce.pokedex.controller;

import ec.edu.uce.pokedex.view.*;
import org.springframework.stereotype.Controller;

@Controller
public class PokedexController {

    private final MainView mainView;
    private final SearchView searchView;
    private final SpriteView spriteView;
    private final HomeView homeView;
    private final StatView statView;
    private final TypeView typeView;
    private final EvolutionView evolutionView;

    public PokedexController(
            MainView mainView,
            SearchView searchView,
            SpriteView spriteView,
            HomeView homeView,
            StatView statView,
            TypeView typeView,
            EvolutionView evolutionView) {
        this.mainView = mainView;
        this.searchView = searchView;
        this.spriteView = spriteView;
        this.homeView = homeView;
        this.statView = statView;
        this.typeView = typeView;
        this.evolutionView = evolutionView;
    }

    /**
     * Inicia la interfaz gráfica de usuario (GUI) de la Pokédex.
     */
    public void startGUI() {
        mainView.initialize(
                searchView,
                spriteView,
                homeView,
                statView,
                typeView,
                evolutionView);
    }
}

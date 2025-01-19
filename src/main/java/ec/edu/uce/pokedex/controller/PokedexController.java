package ec.edu.uce.pokedex.controller;

import ec.edu.uce.pokedex.view.*;
import org.springframework.stereotype.Controller;

@Controller
public class PokedexController {

    private final MainView mainView;
    private final SearchPanel searchPanel;
    private final AbilityView abilityView;
    private final SpriteView spriteView;
    private final ExamplePanel examplePanel;
    private final StatView statView;
    private final TypeView typeView; // Agregar TypeView

    public PokedexController(MainView mainView, SearchPanel searchPanel, AbilityView abilityView, SpriteView spriteView, ExamplePanel examplePanel, StatView statView, TypeView typeView) {
        this.mainView = mainView;
        this.searchPanel = searchPanel;
        this.abilityView = abilityView;
        this.spriteView = spriteView;
        this.examplePanel = examplePanel;
        this.statView = statView;
        this.typeView = typeView; // Inicializar TypeView
    }

    public void startGUI() {
        mainView.initialize(searchPanel, abilityView, spriteView, examplePanel, statView, typeView);
    }
}

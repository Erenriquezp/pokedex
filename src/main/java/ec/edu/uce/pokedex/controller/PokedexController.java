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

    public PokedexController(MainView mainView, SearchPanel searchPanel, AbilityView abilityView, SpriteView spriteView, ExamplePanel examplePanel) {
        this.mainView = mainView;
        this.searchPanel = searchPanel;
        this.abilityView = abilityView;
        this.spriteView = spriteView;
        this.examplePanel = examplePanel;
    }

    public void startGUI() {
        mainView.initialize(searchPanel, abilityView, spriteView, examplePanel);
    }
}

package ec.edu.uce.pokedex.controller;

import ec.edu.uce.pokedex.view.AbilityView;
import ec.edu.uce.pokedex.view.MainView;
import ec.edu.uce.pokedex.view.SearchPanel;
import org.springframework.stereotype.Controller;

@Controller
public class PokedexController {

    private final MainView mainView;
    private final SearchPanel searchPanel;
    private final AbilityView abilityView;

    public PokedexController(MainView mainView, SearchPanel searchPanel, AbilityView abilityView) {
        this.mainView = mainView;
        this.searchPanel = searchPanel;
        this.abilityView = abilityView;
    }

    public void startGUI() {
        mainView.initialize(searchPanel, abilityView);
    }
}

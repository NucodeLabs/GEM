package ru.nucodelabs.gem.view.main;

import javafx.fxml.FXML;
import ru.nucodelabs.gem.view.usercontrols.mainmenubar.MainMenuBar;
import ru.nucodelabs.mvvm.VBView;

public class WelcomeView extends VBView<WelcomeViewModel> {

    @FXML
    private MainMenuBar menuBar;


    public WelcomeView(WelcomeViewModel viewModel) {
        super(viewModel);
        menuBar.getMenuFileOpenEXP().setOnAction(e -> viewModel.switchToMainView());
    }
}

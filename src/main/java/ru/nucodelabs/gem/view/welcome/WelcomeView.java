package ru.nucodelabs.gem.view.welcome;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ru.nucodelabs.gem.view.usercontrols.mainmenubar.MainMenuBar;
import ru.nucodelabs.mvvm.VBView;

public class WelcomeView extends VBView<WelcomeViewModel> {

    @FXML
    private MainMenuBar menuBar;
    @FXML
    private Button openEXPButton;

    public WelcomeView(WelcomeViewModel viewModel) {
        super(viewModel);
        menuBar.getMenuFileOpenEXP().setOnAction(e -> viewModel.switchToMainView());
        menuBar.getMenuViewLegendsVESCurves().setDisable(true);
        openEXPButton.setOnAction(e -> viewModel.switchToMainView());
    }
}

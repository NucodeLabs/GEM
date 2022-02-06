package ru.nucodelabs.gem.view.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import ru.nucodelabs.gem.view.usercontrols.mainmenubar.MainMenuBar;
import ru.nucodelabs.gem.view.usercontrols.misfitstacks.MisfitStacks;
import ru.nucodelabs.gem.view.usercontrols.vescurves.VESCurves;
import ru.nucodelabs.mvvm.VBView;

public class MainSplitLayoutView extends VBView<MainViewModel> {
    @FXML
    public MainMenuBar mainMenuBar;
    @FXML
    public Button prevButton;
    @FXML
    public Text text;
    @FXML
    public Button nextButton;
    @FXML
    public MisfitStacks misfitStacks;
    @FXML
    public VESCurves vesCurves;

    public MainSplitLayoutView(MainViewModel viewModel) {
        super(viewModel);

        viewModel.initModelCurveDragger(vesCurves.getLineChart());

        mainMenuBar.getMenuFileOpenEXP().setOnAction(e -> viewModel.importEXP());
        mainMenuBar.getMenuFileOpenMOD().setOnAction(e -> viewModel.importMOD());
        mainMenuBar.getMenuFileOpenMOD().disableProperty().bind(viewModel.menuFileMODDisabledProperty());
        mainMenuBar.getMenuViewLegendsVESCurves().selectedProperty().bindBidirectional(vesCurves.getLineChart().legendVisibleProperty());

        vesCurves.getLineChart().dataProperty().bindBidirectional(viewModel.vesCurvesDataProperty());
        text.textProperty().bind(viewModel.vesTextProperty());

        misfitStacks.getLineChart().visibleProperty().bind(viewModel.misfitStacksVisibleProperty());
        misfitStacks.getLineChart().dataProperty().bind(viewModel.misfitStacksDataProperty());
    }
}

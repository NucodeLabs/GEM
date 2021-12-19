package ru.nucodelabs.gem.view.main;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import ru.nucodelabs.gem.view.usercontrols.mainmenubar.MainMenuBar;
import ru.nucodelabs.gem.view.usercontrols.misfitstacks.MisfitStacks;
import ru.nucodelabs.gem.view.usercontrols.vescurves.VESCurves;
import ru.nucodelabs.gem.view.usercontrols.vesmisfitsplit.VESMisfitSplit;
import ru.nucodelabs.mvvm.VBView;

public class MainSplitLayoutView extends VBView<MainViewModel> {

    @FXML
    private MainMenuBar menuBar;
    @FXML
    private VESMisfitSplit vesMisfitSplit;
    @FXML
    private MisfitStacks misfitStacks = vesMisfitSplit.getMisfitStacks();
    @FXML
    private VESCurves vesCurves = vesMisfitSplit.getVesCurves();
    @FXML
    private HBox vesMisfitsHBox;

    public MainSplitLayoutView(MainViewModel viewModel) {
        super(viewModel);
        menuBar.getMenuFileOpenEXP().setOnAction(e -> viewModel.importEXPSTT(this.getScene()));
        menuBar.getMenuFileOpenMOD().setOnAction(e -> viewModel.importMOD(this.getScene()));
        menuBar.getMenuFileOpenMOD().disableProperty().bind(viewModel.menuFileMODDisabledProperty());

        vesCurves.getLineChart().dataProperty().bindBidirectional(viewModel.vesCurvesDataProperty());
        vesCurves.getLineChart().visibleProperty().bind(viewModel.vesLineChartVisibilityProperty());
        vesCurves.getText().textProperty().bind(viewModel.vesTextProperty());

        misfitStacks.getLineChart().visibleProperty().bind(viewModel.misfitStacksLineChartVisibilityProperty());
        misfitStacks.getLineChart().dataProperty().bind(viewModel.misfitStacksDataProperty());
    }
}

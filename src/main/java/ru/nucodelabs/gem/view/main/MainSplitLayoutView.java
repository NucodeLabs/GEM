package ru.nucodelabs.gem.view.main;

import javafx.fxml.FXML;
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


    public MainSplitLayoutView(MainViewModel viewModel) {
        super(viewModel);
        viewModel.initModelCurveDragger(vesMisfitSplit.getVesCurves().getLineChart());

        MisfitStacks misfitStacks = vesMisfitSplit.getMisfitStacks();
        VESCurves vesCurves = vesMisfitSplit.getVesCurves();

        menuBar.getMenuFileOpenEXP().setOnAction(e -> viewModel.importEXP(this.getScene()));
        menuBar.getMenuFileOpenMOD().setOnAction(e -> viewModel.importMOD(this.getScene()));
        menuBar.getMenuFileOpenMOD().disableProperty().bind(viewModel.menuFileMODDisabledProperty());

        vesCurves.getLineChart().dataProperty().bindBidirectional(viewModel.vesCurvesDataProperty());
        vesCurves.getLineChart().visibleProperty().bind(viewModel.vesLineChartVisibilityProperty());
        vesCurves.getText().textProperty().bind(viewModel.vesTextProperty());

        misfitStacks.getLineChart().visibleProperty().bind(viewModel.misfitStacksLineChartVisibilityProperty());
        misfitStacks.getLineChart().dataProperty().bind(viewModel.misfitStacksDataProperty());
    }
}

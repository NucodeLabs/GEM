package ru.nucodelabs.gem.view.main;

import javafx.fxml.FXML;
import ru.nucodelabs.gem.view.usercontrols.mainmenubar.MainMenuBar;
import ru.nucodelabs.gem.view.usercontrols.misfitstacks.MisfitStacks;
import ru.nucodelabs.gem.view.usercontrols.vescurves.VESCurves;
import ru.nucodelabs.gem.view.usercontrols.vesmisfitsplit.VESMisfitSplit;
import ru.nucodelabs.mvvm.VBView;

public class MainSplitLayoutView_single extends VBView<MainViewModel> {

    @FXML
    private MainMenuBar menuBar;
    @FXML
    private VESMisfitSplit vesMisfitSplit;


    public MainSplitLayoutView_single(MainViewModel viewModel) {
        super(viewModel);
        viewModel.initModelCurveDragger(vesMisfitSplit.getVesCurves().getLineChart());

        MisfitStacks misfitStacks = vesMisfitSplit.getMisfitStacks();
        VESCurves vesCurves = vesMisfitSplit.getVesCurves();

        menuBar.getMenuFileOpenEXP().setOnAction(e -> viewModel.importEXP());
        menuBar.getMenuFileOpenMOD().setOnAction(e -> viewModel.importMOD());
        menuBar.getMenuFileOpenMOD().disableProperty().bind(viewModel.menuFileMODDisabledProperty());
        menuBar.getMenuViewLegendsVESCurves().selectedProperty().bindBidirectional(viewModel.vesLegendsVisibleProperty());

        vesCurves.getLineChart().legendVisibleProperty().bind(viewModel.vesLegendsVisibleProperty());
        vesCurves.getLineChart().dataProperty().bindBidirectional(viewModel.vesCurvesDataProperty());
        vesCurves.getLineChart().visibleProperty().bind(viewModel.vesLineChartVisibleProperty());

        misfitStacks.getLineChart().visibleProperty().bind(viewModel.misfitStacksLineChartVisibleProperty());
        misfitStacks.getLineChart().dataProperty().bind(viewModel.misfitStacksDataProperty());
    }
}

package ru.nucodelabs.gem.view.main;

import javafx.fxml.FXML;
import ru.nucodelabs.gem.view.usercontrols.mainmenubar.MainMenuBar;
import ru.nucodelabs.gem.view.usercontrols.misfitstacks.MisfitStacks;
import ru.nucodelabs.gem.view.usercontrols.vescurves.VESCurves;
import ru.nucodelabs.gem.view.usercontrols.vescurvestablesplit.VESCurvesTableSplit;
import ru.nucodelabs.mvvm.VBView;

public class MainSplitLayoutView extends VBView<MainViewModel> {
    @FXML
    public MainMenuBar mainMenuBar;
    @FXML
    public VESCurvesTableSplit vesCurvesTable;

    public MainSplitLayoutView(MainViewModel viewModel) {
        super(viewModel);

        viewModel.initModelCurveDragger(vesCurvesTable.getVesCurves().getLineChart());

        MisfitStacks misfitStacks = vesCurvesTable.getMisfitStacks();
        VESCurves vesCurves = vesCurvesTable.getVesCurves();

        mainMenuBar.getMenuFileOpenEXP().setOnAction(e -> viewModel.importEXP());
        mainMenuBar.getMenuFileOpenMOD().setOnAction(e -> viewModel.importMOD());
        mainMenuBar.getMenuFileOpenMOD().disableProperty().bind(viewModel.menuFileMODDisabledProperty());
        mainMenuBar.getMenuViewLegendsVESCurves().selectedProperty().bindBidirectional(vesCurves.getLineChart().legendVisibleProperty());

        vesCurves.getLineChart().dataProperty().bindBidirectional(viewModel.vesCurvesDataProperty());
        vesCurvesTable.getText().textProperty().bind(viewModel.vesTextProperty());

        misfitStacks.getLineChart().visibleProperty().bind(viewModel.misfitStacksVisibleProperty());
        misfitStacks.getLineChart().dataProperty().bind(viewModel.misfitStacksDataProperty());
    }
}

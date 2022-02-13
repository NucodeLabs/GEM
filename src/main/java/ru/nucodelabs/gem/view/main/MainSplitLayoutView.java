package ru.nucodelabs.gem.view.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import ru.nucodelabs.gem.view.usercontrols.mainmenubar.MainMenuBar;
import ru.nucodelabs.gem.view.usercontrols.misfitstacks.MisfitStacks;
import ru.nucodelabs.gem.view.usercontrols.vescurves.VESCurves;
import ru.nucodelabs.gem.view.usercontrols.vestables.EXPTable;
import ru.nucodelabs.mvvm.VBView;

public class MainSplitLayoutView extends VBView<MainViewModel> {
    @FXML
    public MainMenuBar mainMenuBar;
    @FXML
    public Button prevButton;
    @FXML
    public Text vesTitle;
    @FXML
    public Button nextButton;
    @FXML
    public MisfitStacks misfitStacks;
    @FXML
    public VESCurves vesCurves;
    @FXML
    public EXPTable expTable;

    public MainSplitLayoutView(MainViewModel viewModel) {
        super(viewModel);

        viewModel.initModelCurveDragger(vesCurves.getLineChart());

        mainMenuBar.getMenuFileOpenEXP().setOnAction(e -> viewModel.importEXP());
        mainMenuBar.getMenuFileOpenMOD().setOnAction(e -> viewModel.importMOD());
        mainMenuBar.getMenuFileOpenMOD().disableProperty().bind(viewModel.menuFileMODDisabledProperty());
        mainMenuBar.getMenuViewLegendsVESCurves().selectedProperty().bindBidirectional(vesCurves.getLineChart().legendVisibleProperty());

        vesCurves.getLineChart().dataProperty().bindBidirectional(viewModel.vesCurvesDataProperty());
        vesTitle.textProperty().bind(viewModel.vesTextProperty());

        misfitStacks.getLineChart().dataProperty().bind(viewModel.misfitStacksDataProperty());

        expTable.getExperimentalTable().itemsProperty().bind(viewModel.expTableDataProperty());
        System.out.println(viewModel.getExpTableData().toString());
    }
}

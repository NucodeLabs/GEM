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
    public Text vesTitle;
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

        vesCurves.getLineChartXAxis().lowerBoundProperty().bind(viewModel.vesCurvesXLowerBoundProperty());
        vesCurves.getLineChartXAxis().upperBoundProperty().bind(viewModel.vesCurvesXUpperBoundProperty());
        vesCurves.getLineChartYAxis().lowerBoundProperty().bind(viewModel.vesCurvesYLowerBoundProperty());
        vesCurves.getLineChartYAxis().upperBoundProperty().bind(viewModel.vesCurvesYUpperBoundProperty());

        vesCurves.getLeftBtn().setOnAction(e -> viewModel.moveLeftVesCurves());
        vesCurves.getRightBtn().setOnAction(e -> viewModel.moveRightVesCurves());
        vesCurves.getUpBtn().setOnAction(e -> viewModel.moveUpVesCurves());
        vesCurves.getDownBtn().setOnAction(e -> viewModel.moveDownVesCurves());
        vesCurves.getPlusBtn().setOnAction(e -> viewModel.zoomInVesCurves());
        vesCurves.getMinusBtn().setOnAction(e -> viewModel.zoomOutVesCurves());

        vesCurves.getLineChart().dataProperty().bindBidirectional(viewModel.vesCurvesDataProperty());
        vesTitle.textProperty().bind(viewModel.vesTextProperty());

        misfitStacks.getLineChart().dataProperty().bind(viewModel.misfitStacksDataProperty());
        misfitStacks.getLineChartXAxis().lowerBoundProperty().bind(viewModel.vesCurvesXLowerBoundProperty());
        misfitStacks.getLineChartXAxis().upperBoundProperty().bind(viewModel.vesCurvesXUpperBoundProperty());
    }
}

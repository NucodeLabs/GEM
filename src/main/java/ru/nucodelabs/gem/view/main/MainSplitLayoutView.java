package ru.nucodelabs.gem.view.main;

import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import ru.nucodelabs.gem.view.usercontrols.mainmenubar.MainMenuBar;
import ru.nucodelabs.gem.view.usercontrols.misfitstacks.MisfitStacks;
import ru.nucodelabs.gem.view.usercontrols.nofile.NoFileScreen;
import ru.nucodelabs.gem.view.usercontrols.vescurves.VESCurves;
import ru.nucodelabs.gem.view.usercontrols.vestables.ExperimentalTable;
import ru.nucodelabs.gem.view.usercontrols.vestables.ModelTable;
import ru.nucodelabs.mvvm.VBView;

import java.util.Objects;

public class MainSplitLayoutView extends VBView<MainViewModel> {
    @FXML
    private NoFileScreen noFileScreen;
    @FXML
    private MainMenuBar mainMenuBar;
    @FXML
    private Button prevButton;
    @FXML
    private Label picketNumber;
    @FXML
    private Label vesTitle;
    @FXML
    private Button nextButton;
    @FXML
    private MisfitStacks misfitStacks;
    @FXML
    private VESCurves vesCurves;
    @FXML
    private ExperimentalTable experimentalTable;
    @FXML
    private ModelTable modelTable;

    public MainSplitLayoutView(MainViewModel viewModel) {
        super(viewModel);

        viewModel.initModelCurveDragger((pointInScene) ->
                new XYChart.Data<>(
                        (Double) vesCurves
                                .getLineChartXAxis()
                                .getValueForDisplay(
                                        vesCurves.getLineChartXAxis().sceneToLocal(pointInScene).getX()
                                ),
                        (Double) vesCurves
                                .getLineChartYAxis()
                                .getValueForDisplay(
                                        vesCurves.getLineChartYAxis().sceneToLocal(pointInScene).getY()
                                )
                )
        );

        noFileScreen.visibleProperty().bind(viewModel.noFileOpenedProperty());
        noFileScreen.getOpenEXPButton().setOnAction(e -> viewModel.importEXP());
        noFileScreen.getOpenSectionButton().setOnAction(e -> viewModel.openSection());

        mainMenuBar.getMenuFileOpenEXP().setOnAction(e -> viewModel.importEXP());
        mainMenuBar.getMenuFileOpenMOD().setOnAction(e -> viewModel.importMOD());
        mainMenuBar.getMenuFileNewWindow().setOnAction(e -> viewModel.newWindow());
        mainMenuBar.getMenuFileOpenMOD().disableProperty().bind(viewModel.menuFileMODDisabledProperty());
        mainMenuBar.getMenuViewLegendsVESCurves().selectedProperty().bindBidirectional(vesCurves.getLineChart().legendVisibleProperty());
        mainMenuBar.getMenuFileSaveSection().setOnAction(e -> viewModel.saveSection());
        mainMenuBar.getMenuFileOpenSection().setOnAction(e -> viewModel.openSection());
        mainMenuBar.getMenuFileClose().setOnAction(e -> {
            selfClose();
            viewModel.newWindow();
        });
        mainMenuBar.getMenuFileSaveSection().disableProperty().bind(viewModel.noFileOpenedProperty());
        mainMenuBar.getMenuFileSavePicket().disableProperty().bind(viewModel.noFileOpenedProperty());

        nextButton.setOnAction(e -> viewModel.switchToNextPicket());
        prevButton.setOnAction(e -> viewModel.switchToPrevPicket());

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
        vesTitle.textProperty().bind(viewModel.vesTitleProperty());
        picketNumber.textProperty().bind(viewModel.vesNumberProperty());

        misfitStacks.getLineChart().dataProperty().bind(viewModel.misfitStacksDataProperty());
        misfitStacks.getLineChartXAxis().lowerBoundProperty().bind(viewModel.vesCurvesXLowerBoundProperty());
        misfitStacks.getLineChartXAxis().upperBoundProperty().bind(viewModel.vesCurvesXUpperBoundProperty());

        experimentalTable.getExperimentalTable().itemsProperty().bind(viewModel.expTableDataProperty());
        modelTable.getModelTable().itemsProperty().bind(viewModel.modelTableDataProperty());
    }

    public void initShortcutsVESCurvesNavigation() {
        Objects.requireNonNull(this.getScene(), "Scene is null");
        var accelerators = this.getScene().getAccelerators();
        accelerators.put(
                new KeyCodeCombination(KeyCode.LEFT, KeyCombination.SHORTCUT_DOWN),
                viewModel::moveLeftVesCurves
        );
        accelerators.put(
                new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.SHORTCUT_DOWN),
                viewModel::moveRightVesCurves
        );
        accelerators.put(
                new KeyCodeCombination(KeyCode.UP, KeyCombination.SHORTCUT_DOWN),
                viewModel::moveUpVesCurves
        );
        accelerators.put(
                new KeyCodeCombination(KeyCode.DOWN, KeyCombination.SHORTCUT_DOWN),
                viewModel::moveDownVesCurves
        );
        accelerators.put(
                new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.SHORTCUT_DOWN),
                viewModel::zoomInVesCurves
        );
        accelerators.put(
                new KeyCodeCombination(KeyCode.MINUS, KeyCombination.SHORTCUT_DOWN),
                viewModel::zoomOutVesCurves
        );
    }
}

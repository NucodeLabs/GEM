package ru.nucodelabs.gem.view.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.text.Text;
import ru.nucodelabs.gem.view.usercontrols.mainmenubar.MainMenuBar;
import ru.nucodelabs.gem.view.usercontrols.misfitstacks.MisfitStacks;
import ru.nucodelabs.gem.view.usercontrols.vescurves.VESCurves;
import ru.nucodelabs.gem.view.usercontrols.vestables.ExperimentalTable;
import ru.nucodelabs.mvvm.VBView;

import java.util.Objects;

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
    public ExperimentalTable experimentalTable;

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

        experimentalTable.getExperimentalTable().itemsProperty().bind(viewModel.expTableDataProperty());
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

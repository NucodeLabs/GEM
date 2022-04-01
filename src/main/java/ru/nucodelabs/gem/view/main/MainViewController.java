package ru.nucodelabs.gem.view.main;

import com.google.inject.name.Named;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.app.HistoryManager;
import ru.nucodelabs.gem.app.MainViewHelper;
import ru.nucodelabs.gem.utils.OSDetect;
import ru.nucodelabs.gem.view.AbstractController;
import ru.nucodelabs.gem.view.charts.VESCurvesController;

import javax.inject.Inject;
import javax.inject.Provider;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController extends AbstractController {

    private final StringProperty vesTitle = new SimpleStringProperty("");
    private final StringProperty vesNumber = new SimpleStringProperty();
    private final BooleanProperty noFileOpened = new SimpleBooleanProperty(true);

    private final ObservableObjectValue<Picket> picket;
    private final IntegerProperty picketIndex;
    private final ObservableList<Picket> picketObservableList;
    private final MainViewHelper mainViewHelper;

    @FXML
    public CheckMenuItem menuViewVESCurvesLegend;
    @FXML
    private Stage root;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menuView;
    @FXML
    private NoFileScreenController noFileScreenController;
    @Inject
    @Named("MainView")
    private Provider<Stage> mainViewProvider;
    @Inject
    @Named("Save")
    private Provider<Dialog<ButtonType>> saveDialogProvider;
    @Inject
    private HistoryManager historyManager;
    @FXML
    private VESCurvesController vesCurvesController;

    @Inject
    public MainViewController(
            ObservableObjectValue<Picket> picket,
            IntegerProperty picketIndex,
            ObservableList<Picket> picketObservableList,
            MainViewHelper mainViewHelper) {
        this.picket = picket;
        this.picketIndex = picketIndex;
        this.picketObservableList = picketObservableList;
        this.mainViewHelper = mainViewHelper;

        bind();
    }

    private void bind() {
        vesNumber.bind(new StringBinding() {
            {
                super.bind(picketIndex, picketObservableList);
            }

            @Override
            protected String computeValue() {
                return (picketIndex.get() + 1) + "/" + picketObservableList.size();
            }
        });

        vesTitle.bind(new StringBinding() {
            {
                super.bind(picket);
            }

            @Override
            protected String computeValue() {
                return picket.get() != null ? picket.get().name() : "-";
            }
        });

        noFileOpened.bind(new BooleanBinding() {
            {
                super.bind(picketObservableList);
            }

            @Override
            protected boolean computeValue() {
                return picketObservableList.isEmpty();
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getStage().setOnCloseRequest(mainViewHelper::askToSave);
        getStage().getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN),
                this::redo
        );

        mainViewHelper.setStage(getStage());
        noFileScreenController.visibleProperty().bind(noFileOpened);
        vesCurvesController.legendVisibleProperty().bind(menuViewVESCurvesLegend.selectedProperty());

        if (OSDetect.isMacOS()) {
            CheckMenuItem useSystemMenu = new CheckMenuItem(resources.getString("useSystemMenu"));
            menuView.getItems().add(0, useSystemMenu);
            useSystemMenu.selectedProperty().bindBidirectional(menuBar.useSystemMenuBarProperty());
        }
    }

    @Override
    protected Stage getStage() {
        return root;
    }

    @FXML
    public void closeFile(Event event) {
        mainViewHelper.closeFile(event);
    }

    @FXML
    public void importEXP() {
        mainViewHelper.importEXP();
    }

    @FXML
    public void openSection(Event event) {
        mainViewHelper.openSection(event);
    }

    @FXML
    public void saveSection() {
        mainViewHelper.saveSection();
    }

    @FXML
    public void saveSectionAs() {
        mainViewHelper.saveSectionAs();
    }

    /**
     * Opens new window
     */
    @FXML
    public void newWindow() {
        mainViewProvider.get().show();
    }

    /**
     * Asks which file to import and then import it
     */
    @FXML
    public void importMOD() {
        mainViewHelper.importMOD();
    }

    @FXML
    public void importJsonPicket() {
        mainViewHelper.importJsonPicket();
    }

    @FXML
    public void exportJsonPicket() {
        mainViewHelper.exportJsonPicket();
    }

    @FXML
    public void switchToNextPicket() {
        if (picketIndex.get() + 1 <= picketObservableList.size() - 1
                && !picketObservableList.isEmpty()) {
            picketIndex.set(picketIndex.get() + 1);
        }
    }

    @FXML
    public void switchToPrevPicket() {
        if (picketIndex.get() >= 1
                && !picketObservableList.isEmpty()) {
            picketIndex.set(picketIndex.get() - 1);
        }
    }

    @FXML
    public void inverseSolve() {
        mainViewHelper.inverseSolve();
    }

    public String getVesTitle() {
        return vesTitle.get();
    }

    public StringProperty vesTitleProperty() {
        return vesTitle;
    }

    public boolean getNoFileOpened() {
        return noFileOpened.get();
    }

    public BooleanProperty noFileOpenedProperty() {
        return noFileOpened;
    }

    public String getVesNumber() {
        return vesNumber.get();
    }

    public StringProperty vesNumberProperty() {
        return vesNumber;
    }

    public void undo() {
        historyManager.undo();
    }

    public void redo() {
        historyManager.redo();
    }
}

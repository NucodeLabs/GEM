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
import ru.nucodelabs.gem.app.AppService;
import ru.nucodelabs.gem.app.annotation.State;
import ru.nucodelabs.gem.utils.FXUtils;
import ru.nucodelabs.gem.utils.OSDetect;
import ru.nucodelabs.gem.view.AbstractController;
import ru.nucodelabs.gem.view.charts.VESCurvesController;

import javax.inject.Inject;
import javax.inject.Provider;
import java.net.URL;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;

public class MainViewController extends AbstractController {

    private final StringProperty vesTitle = new SimpleStringProperty("");
    private final StringProperty vesNumber = new SimpleStringProperty();
    private final BooleanProperty noFileOpened = new SimpleBooleanProperty(true);

    private final ObservableObjectValue<Picket> picket;
    private final IntegerProperty picketIndex;
    private final ObservableList<Picket> picketObservableList;
    private final AppService appService;

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
    private ResourceBundle uiProperties;
    @Inject
    @Named("MainView")
    private Provider<Stage> mainViewProvider;
    @Inject
    @Named("Save")
    private Provider<Dialog<ButtonType>> saveDialogProvider;
    @FXML
    private VESCurvesController vesCurvesController;

    @Inject
    public MainViewController(
            ObservableObjectValue<Picket> picket,
            IntegerProperty picketIndex,
            @State ObservableList<Picket> picketObservableList,
            AppService appService) {
        this.picket = picket;
        this.picketIndex = picketIndex;
        this.picketObservableList = picketObservableList;
        this.appService = appService;

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
        getStage().setOnCloseRequest(appService::askToSave);
        getStage().getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN),
                this::redo
        );

        appService.setStage(getStage());
        uiProperties = requireNonNull(resources);
        noFileScreenController.visibleProperty().bind(noFileOpened);
        vesCurvesController.legendVisibleProperty().bind(menuViewVESCurvesLegend.selectedProperty());

        if (OSDetect.isMacOS()) {
            CheckMenuItem useSystemMenu = new CheckMenuItem(uiProperties.getString("useSystemMenu"));
            menuView.getItems().add(0, useSystemMenu);
            useSystemMenu.selectedProperty().bindBidirectional(menuBar.useSystemMenuBarProperty());
            FXUtils.addCloseShortcutMacOS(getStage().getScene().getRoot());
        }
    }

    @Override
    protected Stage getStage() {
        return root;
    }

    @FXML
    public void closeFile(Event event) {
        appService.closeFile(event);
    }

    @FXML
    public void importEXP() {
        appService.importEXP();
    }

    @FXML
    public void openSection(Event event) {
        appService.openSection(event);
    }

    @FXML
    public void saveSection() {
        appService.saveSection();
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
        appService.importMOD();
    }

    @FXML
    public void importJsonPicket() {
        appService.importJsonPicket();
    }

    @FXML
    public void exportJsonPicket() {
        appService.exportJsonPicket();
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
        appService.inverseSolve();
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
        appService.undo();
    }

    public void redo() {
        appService.redo();
    }
}

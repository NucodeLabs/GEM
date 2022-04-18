package ru.nucodelabs.gem.view.tables;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.ModelLayer;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.app.model.SectionManager;
import ru.nucodelabs.gem.app.snapshot.HistoryManager;
import ru.nucodelabs.gem.view.AlertsFactory;
import ru.nucodelabs.gem.view.main.MainViewController;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import static java.lang.Math.min;

public class ModelTableController extends AbstractEditableTableController {

    private final ObservableObjectValue<Picket> picket;

    @FXML
    private TableColumn<Object, Integer> indexCol;
    @FXML
    private TableColumn<ModelLayer, Double> powerCol;
    @FXML
    private TableColumn<ModelLayer, Double> resistanceCol;
    @FXML
    private TextField powerTextField;
    @FXML
    private TextField resistanceTextField;
    @FXML
    private TextField indexTextField;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button addBtn;
    @FXML
    private TableView<ModelLayer> table;

    @Inject
    private Provider<MainViewController> mainViewControllerProvider;
    @Inject
    private AlertsFactory alertsFactory;
    @Inject
    private Validator validator;
    @Inject
    private SectionManager sectionManager;
    @Inject
    private HistoryManager historyManager;
    @Inject
    private IntegerProperty picketIndex;
    private List<TextField> requiredForAdd;

    @Inject
    public ModelTableController(ObservableObjectValue<Picket> picket) {
        this.picket = picket;

        picket.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (oldValue != null
                        && !oldValue.getModelData().equals(newValue.getModelData())) {
                    update();
                } else if (oldValue == null) {
                    update();
                }
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL location, ResourceBundle resources) {
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.getSelectionModel().getSelectedItems()
                .addListener((ListChangeListener<? super ModelLayer>) c -> {
                    if (c.next()) {
                        deleteBtn.setDisable(c.getList().isEmpty());
                    }
                });

        indexCol.setCellFactory(Tables.indexCellFactory());

        powerCol.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue().getPower()));
        resistanceCol.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue().getResistance()));

        for (int i = 1; i < table.getColumns().size(); i++) {
            // safe cast
            ((TableColumn<ModelLayer, Double>) table.getColumns().get(i))
                    .setCellFactory(TextFieldTableCell.forTableColumn(Tables.doubleStringConverter()));
        }

        addIndexInputCheckListener(indexTextField);

        requiredForAdd = List.of(powerTextField, resistanceTextField);
        addDataInputCheckListener(resistanceTextField);
        addDataInputCheckListener(powerTextField);

        addEnterKeyHandler(indexTextField);
        addEnterKeyHandler(resistanceTextField);
        addEnterKeyHandler(powerTextField);

        table.itemsProperty().addListener((observable, oldValue, newValue) -> {
            newValue.addListener((ListChangeListener<? super ModelLayer>) c -> table.refresh());
            table.refresh();
        });
    }

    @Override
    protected Stage getStage() {
        return (Stage) table.getScene().getWindow();
    }

    protected void update() {
        table.itemsProperty().setValue(FXCollections.observableList(picket.get().getModelData()));
    }

    @FXML
    private void onEditCommit(TableColumn.CellEditEvent<ModelLayer, Double> event) {
        int index = event.getTablePosition().getRow();
        var column = event.getTableColumn();
        ModelLayer oldValue = event.getRowValue();
        ModelLayer newValue;
        double newInputValue = event.getNewValue();

        if (column == powerCol) {
            newValue = ModelLayer.create(newInputValue, oldValue.getResistance());
        } else if (column == resistanceCol) {
            newValue = ModelLayer.create(oldValue.getPower(), newInputValue);
        } else {
            throw new RuntimeException("Something went wrong!");
        }

        List<ModelLayer> newModelData = new ArrayList<>(picket.get().getModelData());

        newModelData.set(index, newValue);

        if (!event.getNewValue().isNaN()) {
            setIfValidElseAlert(newModelData);
        } else {
            table.refresh();
        }
    }

    @FXML
    private void addLayer() {
        if (requiredForAdd.stream().noneMatch(textField -> textField.getText().isBlank())) {

            double newResistanceValue = Double.parseDouble(resistanceTextField.getText());
            double newPowerValue = Double.parseDouble(powerTextField.getText());

            int index = picket.get().getModelData().size();

            int inputIndex = index;
            try {
                inputIndex = Integer.parseInt(indexTextField.getText());
            } catch (NumberFormatException ignored) {
            }

            index = min(index, inputIndex);

            List<ModelLayer> newModelData = new ArrayList<>(picket.get().getModelData());
            newModelData.add(index, ModelLayer.create(newPowerValue, newResistanceValue));

            setIfValidElseAlert(newModelData);
        }
    }

    @FXML
    private void deleteSelected() {
        List<ModelLayer> newModelData = deleteIndices(
                table.getSelectionModel().getSelectedIndices(),
                picket.get().getModelData());
        setIfValidElseAlert(newModelData);
    }

    private void setIfValidElseAlert(List<ModelLayer> newModelData) {
        Picket test = Picket.create(picket.get().getName(), picket.get().getExperimentalData(), newModelData);
        Set<ConstraintViolation<Picket>> violations = validator.validate(test);

        if (!violations.isEmpty()) {
            alertsFactory.violationsAlert(violations, getStage()).show();
            table.refresh();
        } else {
            historyManager.performThenSnapshot(
                    () -> sectionManager.updateModelData(picketIndex.get(), newModelData));
        }
    }

    @FXML
    private void importModel() {
        mainViewControllerProvider.get().importMOD();
    }

    @FXML
    private void dragOverHandle(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            List<File> files = dragEvent.getDragboard().getFiles();
            for (var file : files) {
                if (file.getName().endsWith(".MOD") || file.getName().endsWith(".mod")) {
                    dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
            }
        }
        dragEvent.consume();
    }

    @FXML
    private void dragDropHandle(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            List<File> files = dragEvent.getDragboard().getFiles();
            dragEvent.setDropCompleted(true);
            dragEvent.consume();
            for (var file : files) {
                if (file.getName().endsWith(".MOD") || file.getName().endsWith(".mod")) {
                    mainViewControllerProvider.get().importMOD(file);
                }
            }
        }
    }

    @Override
    protected List<TextField> getRequiredForAdd() {
        return requiredForAdd;
    }

    @Override
    protected Button getAddButton() {
        return addBtn;
    }
}

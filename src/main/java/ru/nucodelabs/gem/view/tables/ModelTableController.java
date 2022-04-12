package ru.nucodelabs.gem.view.tables;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.ModelDataRow;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.app.HistoryManager;
import ru.nucodelabs.gem.app.model.SectionManager;
import ru.nucodelabs.gem.view.AlertsFactory;
import ru.nucodelabs.gem.view.main.MainViewController;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;
import java.net.URL;
import java.util.*;

import static java.lang.Math.min;

public class ModelTableController extends AbstractEditableTableController {

    private final ObservableObjectValue<Picket> picket;

    @FXML
    private VBox dragDropPlaceholder;
    @FXML
    private TextField powerTextField;
    @FXML
    private TextField resistanceTextField;
    @FXML
    private TextField polarizationTextField;
    @FXML
    private TextField indexTextField;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button addBtn;
    @FXML
    private TableView<ModelDataRow> table;

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
                        && !oldValue.modelData().equals(newValue.modelData())) {
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
                .addListener((ListChangeListener<? super ModelDataRow>) c -> {
                    if (c.next()) {
                        deleteBtn.setDisable(c.getList().isEmpty());
                    }
                });
        for (int i = 1; i < table.getColumns().size(); i++) {
            // safe cast
            ((TableColumn<ModelDataRow, Double>) table.getColumns().get(i))
                    .setCellFactory(TextFieldTableCell.forTableColumn(Tables.doubleStringConverter()));
        }

        addIndexInputCheckListener(indexTextField);

        requiredForAdd = List.of(powerTextField, resistanceTextField, polarizationTextField);
        addDataInputCheckListener(polarizationTextField);
        addDataInputCheckListener(resistanceTextField);
        addDataInputCheckListener(powerTextField);

        addEnterKeyHandler(indexTextField);
        addEnterKeyHandler(polarizationTextField);
        addEnterKeyHandler(resistanceTextField);
        addEnterKeyHandler(powerTextField);
    }

    @Override
    protected Stage getStage() {
        return (Stage) table.getScene().getWindow();
    }

    protected void update() {
        ObservableList<ModelDataRow> modelDataRows = FXCollections.observableList(picket.get().modelData().getRows());
        table.itemsProperty().setValue(modelDataRows);
    }

    @FXML
    private void onPowerEditCommit(TableColumn.CellEditEvent<ModelDataRow, Double> event) {
        int index = event.getRowValue().index();
        List<Double> newPower = new ArrayList<>(picket.get().modelData().power());
        newPower.set(index, event.getNewValue());
        ModelData newModelData = new ModelData(
                picket.get().modelData().resistance(),
                picket.get().modelData().polarization(),
                newPower
        );
        if (!event.getNewValue().isNaN()) {
            setIfValidElseAlert(newModelData);
        } else {
            table.refresh();
        }
    }

    @FXML
    private void onResistanceEditCommit(TableColumn.CellEditEvent<ModelDataRow, Double> event) {
        int index = event.getRowValue().index();
        List<Double> newResistance = new ArrayList<>(picket.get().modelData().resistance());
        newResistance.set(index, event.getNewValue());
        ModelData newModelData = new ModelData(
                newResistance,
                picket.get().modelData().polarization(),
                picket.get().modelData().power()
        );

        if (!event.getNewValue().isNaN()) {
            setIfValidElseAlert(newModelData);
        } else {
            table.refresh();
        }
    }

    @FXML
    private void onPolarizationEditCommit(TableColumn.CellEditEvent<ModelDataRow, Double> event) {
        int index = event.getRowValue().index();
        List<Double> newPolarization = new ArrayList<>(picket.get().modelData().resistance());
        newPolarization.set(index, event.getNewValue());
        ModelData newModelData = new ModelData(
                picket.get().modelData().resistance(),
                newPolarization,
                picket.get().modelData().power()
        );
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
            double newPolarizationValue = Double.parseDouble(polarizationTextField.getText());

            List<Double> newResistance;
            List<Double> newPower;
            List<Double> newPolarization;

            if (picket.get().modelData() == null) {
                newResistance = new ArrayList<>();
                newPower = new ArrayList<>();
                newPolarization = new ArrayList<>();
            } else {
                newResistance = new ArrayList<>(picket.get().modelData().resistance());
                newPower = new ArrayList<>(picket.get().modelData().power());
                newPolarization = new ArrayList<>(picket.get().modelData().polarization());
            }

            int index = picket.get().modelData().size() + 1;

            try {
                index = Integer.parseInt(indexTextField.getText());
            } catch (NumberFormatException ignored) {
            }

            index = min(index, newPower.size());
            newResistance.add(index, newResistanceValue);
            newPower.add(index, newPowerValue);
            newPolarization.add(index, newPolarizationValue);


            ModelData newModelData = new ModelData(
                    newResistance,
                    newPolarization,
                    newPower
            );

            setIfValidElseAlert(newModelData);
        }
    }

    @FXML
    private void deleteSelected() {
        List<ModelDataRow> selectedRows = table.getSelectionModel().getSelectedItems();
        List<Double> newResistance = new ArrayList<>(picket.get().modelData().resistance());
        List<Double> newPower = new ArrayList<>(picket.get().modelData().power());
        List<Double> newPolarization = new ArrayList<>(picket.get().modelData().polarization());

        List<Integer> indicesToRemove = selectedRows.stream()
                .map(ModelDataRow::index)
                .sorted(Collections.reverseOrder())
                .toList();

        indicesToRemove.forEach(i -> {
            int index = i;
            newResistance.remove(index);
            newPolarization.remove(index);
            newPower.remove(index);
        });

        setIfValidElseAlert(new ModelData(newResistance, newPolarization, newPower));
    }

    private void setIfValidElseAlert(ModelData newModelData) {
        Set<ConstraintViolation<ModelData>> violations = validator.validate(newModelData);
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

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
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.ExperimentalMeasurement;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.app.model.SectionManager;
import ru.nucodelabs.gem.app.snapshot.HistoryManager;
import ru.nucodelabs.gem.view.AlertsFactory;

import javax.inject.Inject;
import java.net.URL;
import java.util.*;

import static java.lang.Math.min;

public class ExperimentalTableController extends AbstractEditableTableController {

    private final ObservableObjectValue<Picket> picket;

    @FXML
    private TableColumn<Object, Integer> indexCol;
    @FXML
    private TableColumn<ExperimentalMeasurement, Double> ab2Col;
    @FXML
    private TableColumn<ExperimentalMeasurement, Double> mn2Col;
    @FXML
    private TableColumn<ExperimentalMeasurement, Double> resistanceApparentCol;
    @FXML
    private TableColumn<ExperimentalMeasurement, Double> errorResistanceCol;
    @FXML
    private TableColumn<ExperimentalMeasurement, Double> amperageCol;
    @FXML
    private TableColumn<ExperimentalMeasurement, Double> voltageCol;
    @FXML
    private TextField indexTextField;
    @FXML
    private TextField ab2TextField;
    @FXML
    private TextField mn2TextField;
    @FXML
    private TextField resAppTextField;
    @FXML
    private TextField errResAppTextField;
    @FXML
    private TextField amperageTextField;
    @FXML
    private TextField voltageTextField;

    @FXML
    private Button addBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private TableView<ExperimentalMeasurement> table;

    private List<TextField> requiredForAdd;

    @Inject
    private Validator validator;
    @Inject
    private IntegerProperty picketIndex;
    @Inject
    private SectionManager sectionManager;
    @Inject
    private HistoryManager historyManager;
    @Inject
    private AlertsFactory alertsFactory;

    @Inject
    public ExperimentalTableController(ObservableObjectValue<Picket> picket) {
        this.picket = picket;
        picket.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (oldValue != null
                        && !oldValue.getExperimentalData().equals(newValue.getExperimentalData())) {
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
        requiredForAdd = List.of(
                ab2TextField,
                mn2TextField,
                resAppTextField,
                errResAppTextField,
                amperageTextField,
                voltageTextField);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.getSelectionModel().getSelectedItems()
                .addListener((ListChangeListener<? super ExperimentalMeasurement>) c -> {
                    if (c.next()) {
                        deleteBtn.setDisable(c.getList().isEmpty());
                    }
                });

        indexCol.setCellFactory(Tables.indexCellFactory());

        ab2Col.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue().getAb2()));
        mn2Col.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue().getMn2()));
        resistanceApparentCol.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue().getResistanceApparent()));
        errorResistanceCol.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue().geErrorResistanceApparent()));
        amperageCol.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue().getAmperage()));
        voltageCol.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue().getVoltage()));

        for (int i = 1; i < table.getColumns().size(); i++) {
            // safe cast
            ((TableColumn<ExperimentalMeasurement, Double>) table.getColumns().get(i))
                    .setCellFactory(TextFieldTableCell.forTableColumn(Tables.doubleStringConverter()));
        }

        requiredForAdd.forEach(this::addDataInputCheckListener);
        requiredForAdd.forEach(this::addEnterKeyHandler);
        addIndexInputCheckListener(indexTextField);
    }

    @Override
    protected Stage getStage() {
        return (Stage) table.getScene().getWindow();
    }

    protected void update() {
        table.itemsProperty().setValue(FXCollections.observableList(picket.get().getExperimentalData()));
    }


    @FXML
    private void deleteSelected() {
        List<ExperimentalMeasurement> selectedRows = table.getSelectionModel().getSelectedItems();

        List<Integer> indicesToRemove = selectedRows.stream()
                .map(experimentalMeasurement -> picket.get().getExperimentalData().indexOf(experimentalMeasurement))
                .sorted(Collections.reverseOrder())
                .toList();

        List<ExperimentalMeasurement> newExpData = new ArrayList<>(picket.get().getExperimentalData());
        indicesToRemove.forEach(i -> newExpData.remove(i.intValue()));

        setIfValidElseAlert(newExpData);
    }

    @FXML
    private void add() {
        if (requiredForAdd.stream().noneMatch(textField -> textField.getText().isBlank())) {

            double newAb2Value = Double.parseDouble(ab2TextField.getText());
            double newMn2Value = Double.parseDouble(mn2TextField.getText());
            double newResAppValue = Double.parseDouble(resAppTextField.getText());
            double newErrResAppValue = Double.parseDouble(errResAppTextField.getText());
            double newAmperageValue = Double.parseDouble(amperageTextField.getText());
            double newVoltageValue = Double.parseDouble(voltageTextField.getText());

            int index = picket.get().getExperimentalData().size() + 1;

            try {
                index = Integer.parseInt(indexTextField.getText());
            } catch (NumberFormatException ignored) {
            }

            index = min(index, picket.get().getExperimentalData().size());

            List<ExperimentalMeasurement> experimentalData = new ArrayList<>(picket.get().getExperimentalData());

            experimentalData.add(index, ExperimentalMeasurement.create(
                    newAb2Value, newMn2Value, newResAppValue, newErrResAppValue, newAmperageValue, newVoltageValue
            ));

            setIfValidElseAlert(experimentalData);
        }
    }


    private void setIfValidElseAlert(List<ExperimentalMeasurement> newExpData) {
        Picket test = Picket.create(picket.get().getName(), newExpData, picket.get().getModelData());

        Set<ConstraintViolation<Picket>> violations = validator.validate(test);

        if (!violations.isEmpty()) {
            alertsFactory.violationsAlert(violations, getStage()).show();
            table.refresh();
        } else {
            historyManager.performThenSnapshot(
                    () -> sectionManager.updateExperimentalData(picketIndex.get(), newExpData));
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

    @FXML
    private void onEditCommit(TableColumn.CellEditEvent<ExperimentalMeasurement, Double> event) {
        int index = event.getTablePosition().getRow();

        double newInputValue = event.getNewValue();
        ExperimentalMeasurement oldValue = event.getRowValue();
        ExperimentalMeasurement newValue;
        var column = event.getTableColumn();
        if (column == ab2Col) {
            newValue = ExperimentalMeasurement.create(
                    newInputValue,
                    oldValue.getMn2(),
                    oldValue.getResistanceApparent(),
                    oldValue.geErrorResistanceApparent(),
                    oldValue.getAmperage(),
                    oldValue.getVoltage()
            );
        } else if (column == mn2Col) {
            newValue = ExperimentalMeasurement.create(
                    oldValue.getAb2(),
                    newInputValue,
                    oldValue.getResistanceApparent(),
                    oldValue.geErrorResistanceApparent(),
                    oldValue.getAmperage(),
                    oldValue.getVoltage()
            );
        } else if (column == resistanceApparentCol) {
            newValue = ExperimentalMeasurement.create(
                    oldValue.getAb2(),
                    oldValue.getMn2(),
                    newInputValue,
                    oldValue.geErrorResistanceApparent(),
                    oldValue.getAmperage(),
                    oldValue.getVoltage()
            );
        } else if (column == errorResistanceCol) {
            newValue = ExperimentalMeasurement.create(
                    oldValue.getAb2(),
                    oldValue.getMn2(),
                    oldValue.getResistanceApparent(),
                    newInputValue,
                    oldValue.getAmperage(),
                    oldValue.getVoltage()
            );
        } else if (column == amperageCol) {
            newValue = ExperimentalMeasurement.create(
                    oldValue.getAb2(),
                    oldValue.getMn2(),
                    oldValue.getResistanceApparent(),
                    oldValue.geErrorResistanceApparent(),
                    newInputValue,
                    oldValue.getVoltage()
            );
        } else if (column == voltageCol) {
            newValue = ExperimentalMeasurement.create(
                    newInputValue,
                    oldValue.getMn2(),
                    oldValue.getResistanceApparent(),
                    oldValue.geErrorResistanceApparent(),
                    oldValue.getAmperage(),
                    newInputValue
            );
        } else {
            throw new RuntimeException("Something went wrong!");
        }

        List<ExperimentalMeasurement> newExpData = new ArrayList<>(picket.get().getExperimentalData());

        newExpData.set(index, newValue);

        if (!event.getNewValue().isNaN()) {
            setIfValidElseAlert(newExpData);
        } else {
            table.refresh();
        }
    }
}

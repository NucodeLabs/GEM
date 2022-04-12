package ru.nucodelabs.gem.view.tables;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ExperimentalDataRow;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.app.HistoryManager;
import ru.nucodelabs.gem.app.model.SectionManager;
import ru.nucodelabs.gem.view.AlertsFactory;

import javax.inject.Inject;
import java.net.URL;
import java.util.*;

import static java.lang.Math.min;

public class ExperimentalTableController extends AbstractEditableTableController {

    private final ObservableObjectValue<Picket> picket;

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
    private TextField polarizationAppTextField;
    @FXML
    private TextField errorPolarizationAppTextField;

    @FXML
    private Button addBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private TableView<ExperimentalDataRow> table;

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
                        && !oldValue.experimentalData().equals(newValue.experimentalData())) {
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
                voltageTextField,
                polarizationAppTextField,
                errorPolarizationAppTextField);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.getSelectionModel().getSelectedItems()
                .addListener((ListChangeListener<? super ExperimentalDataRow>) c -> {
                    if (c.next()) {
                        deleteBtn.setDisable(c.getList().isEmpty());
                    }
                });
        for (int i = 1; i < table.getColumns().size(); i++) {
            // safe cast
            ((TableColumn<ExperimentalDataRow, Double>) table.getColumns().get(i))
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
        table.itemsProperty().setValue(FXCollections.observableList(picket.get().experimentalData().getRows()));
    }


    @FXML
    private void deleteSelected() {
        List<ExperimentalDataRow> selectedRows = table.getSelectionModel().getSelectedItems();

        var newAb2 = new ArrayList<>(picket.get().experimentalData().ab_2());
        var newMn2 = new ArrayList<>(picket.get().experimentalData().mn_2());
        var newResistanceApp = new ArrayList<>(picket.get().experimentalData().resistanceApparent());
        var newErrResApp = new ArrayList<>(picket.get().experimentalData().errorResistanceApparent());
        var newAmperage = new ArrayList<>(picket.get().experimentalData().amperage());
        var newVoltage = new ArrayList<>(picket.get().experimentalData().voltage());
        var newPol = new ArrayList<>(picket.get().experimentalData().polarizationApparent());
        var newErrPol = new ArrayList<>(picket.get().experimentalData().errorPolarizationApparent());

        List<Integer> indicesToRemove = selectedRows.stream()
                .map(ExperimentalDataRow::index)
                .sorted(Collections.reverseOrder())
                .toList();

        indicesToRemove.forEach(i -> {
            int index = i;
            newAb2.remove(index);
            newMn2.remove(index);
            newResistanceApp.remove(index);
            newErrResApp.remove(index);
            newAmperage.remove(index);
            newVoltage.remove(index);
            newPol.remove(index);
            newErrPol.remove(index);
        });

        setIfValidElseAlert(new ExperimentalData(
                newAb2,
                newMn2,
                newAmperage,
                newVoltage,
                newResistanceApp,
                newErrResApp,
                newPol,
                newErrPol
        ));
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
            double newPolAppValue = Double.parseDouble(polarizationAppTextField.getText());
            double newErrPolAppValue = Double.parseDouble(errorPolarizationAppTextField.getText());

            List<Double> newAb2;
            List<Double> newMn2;
            List<Double> newResistanceApp;
            List<Double> newErrResApp;
            List<Double> newAmperage;
            List<Double> newVoltage;
            List<Double> newPol;
            List<Double> newErrPol;

            if (picket.get().experimentalData() == null) {
                newAb2 = new ArrayList<>();
                newMn2 = new ArrayList<>();
                newResistanceApp = new ArrayList<>();
                newErrResApp = new ArrayList<>();
                newAmperage = new ArrayList<>();
                newVoltage = new ArrayList<>();
                newPol = new ArrayList<>();
                newErrPol = new ArrayList<>();
            } else {
                newAb2 = new ArrayList<>(picket.get().experimentalData().ab_2());
                newMn2 = new ArrayList<>(picket.get().experimentalData().mn_2());
                newResistanceApp = new ArrayList<>(picket.get().experimentalData().resistanceApparent());
                newErrResApp = new ArrayList<>(picket.get().experimentalData().errorResistanceApparent());
                newAmperage = new ArrayList<>(picket.get().experimentalData().amperage());
                newVoltage = new ArrayList<>(picket.get().experimentalData().voltage());
                newPol = new ArrayList<>(picket.get().experimentalData().polarizationApparent());
                newErrPol = new ArrayList<>(picket.get().experimentalData().errorPolarizationApparent());
            }

            int index = picket.get().experimentalData().size() + 1;

            try {
                index = Integer.parseInt(indexTextField.getText());
            } catch (NumberFormatException ignored) {
            }

            index = min(index, newAb2.size());

            newAb2.add(index, newAb2Value);
            newMn2.add(index, newMn2Value);
            newResistanceApp.add(index, newResAppValue);
            newErrResApp.add(index, newErrResAppValue);
            newAmperage.add(index, newAmperageValue);
            newVoltage.add(index, newVoltageValue);
            newPol.add(index, newPolAppValue);
            newErrPol.add(index, newErrPolAppValue);

            ExperimentalData experimentalData = new ExperimentalData(
                    newAb2,
                    newMn2,
                    newAmperage,
                    newVoltage,
                    newResistanceApp,
                    newErrResApp,
                    newPol,
                    newErrPol
            );

            setIfValidElseAlert(experimentalData);
        }
    }


    private void setIfValidElseAlert(ExperimentalData newExpData) {
        Set<ConstraintViolation<ExperimentalData>> violations = validator.validate(newExpData);
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
    private void onResistanceApparentEditCommit(TableColumn.CellEditEvent<ExperimentalDataRow, Double> event) {
        int index = event.getRowValue().index();
        List<Double> newResApp = new ArrayList<>(picket.get().experimentalData().resistanceApparent());
        newResApp.set(index, event.getNewValue());
        ExperimentalData newExpData = new ExperimentalData(
                picket.get().experimentalData().ab_2(),
                picket.get().experimentalData().mn_2(),
                picket.get().experimentalData().amperage(),
                picket.get().experimentalData().voltage(),
                newResApp,
                picket.get().experimentalData().errorResistanceApparent(),
                picket.get().experimentalData().polarizationApparent(),
                picket.get().experimentalData().errorPolarizationApparent()
        );
        if (!event.getNewValue().isNaN()) {
            setIfValidElseAlert(newExpData);
        } else {
            table.refresh();
        }
    }

    @FXML
    private void onErrorResistanceApparentEditCommit(TableColumn.CellEditEvent<ExperimentalDataRow, Double> event) {
        int index = event.getRowValue().index();
        List<Double> newErrorResApp = new ArrayList<>(picket.get().experimentalData().errorResistanceApparent());
        newErrorResApp.set(index, event.getNewValue());
        ExperimentalData newExpData = new ExperimentalData(
                picket.get().experimentalData().ab_2(),
                picket.get().experimentalData().mn_2(),
                picket.get().experimentalData().amperage(),
                picket.get().experimentalData().voltage(),
                picket.get().experimentalData().resistanceApparent(),
                newErrorResApp,
                picket.get().experimentalData().polarizationApparent(),
                picket.get().experimentalData().errorPolarizationApparent()
        );
        if (!event.getNewValue().isNaN()) {
            setIfValidElseAlert(newExpData);
        } else {
            table.refresh();
        }
    }

    @FXML
    private void onAb2EditCommit(TableColumn.CellEditEvent<ExperimentalDataRow, Double> event) {
        int index = event.getRowValue().index();
        List<Double> newAb2 = new ArrayList<>(picket.get().experimentalData().ab_2());
        newAb2.set(index, event.getNewValue());
        ExperimentalData newExpData = new ExperimentalData(
                newAb2,
                picket.get().experimentalData().mn_2(),
                picket.get().experimentalData().amperage(),
                picket.get().experimentalData().voltage(),
                picket.get().experimentalData().resistanceApparent(),
                picket.get().experimentalData().errorResistanceApparent(),
                picket.get().experimentalData().polarizationApparent(),
                picket.get().experimentalData().errorPolarizationApparent()
        );
        if (!event.getNewValue().isNaN()) {
            setIfValidElseAlert(newExpData);
        } else {
            table.refresh();
        }
    }

    @FXML
    private void onMn2EditCommit(TableColumn.CellEditEvent<ExperimentalDataRow, Double> event) {
        int index = event.getRowValue().index();
        List<Double> newMn2 = new ArrayList<>(picket.get().experimentalData().mn_2());
        newMn2.set(index, event.getNewValue());
        ExperimentalData newExpData = new ExperimentalData(
                picket.get().experimentalData().ab_2(),
                newMn2,
                picket.get().experimentalData().amperage(),
                picket.get().experimentalData().voltage(),
                picket.get().experimentalData().resistanceApparent(),
                picket.get().experimentalData().errorResistanceApparent(),
                picket.get().experimentalData().polarizationApparent(),
                picket.get().experimentalData().errorPolarizationApparent()
        );
        if (!event.getNewValue().isNaN()) {
            setIfValidElseAlert(newExpData);
        } else {
            table.refresh();
        }
    }

    @FXML
    private void onAmperageEditCommit(TableColumn.CellEditEvent<ExperimentalDataRow, Double> event) {
        int index = event.getRowValue().index();
        List<Double> newAmperage = new ArrayList<>(picket.get().experimentalData().amperage());
        newAmperage.set(index, event.getNewValue());
        ExperimentalData newExpData = new ExperimentalData(
                picket.get().experimentalData().ab_2(),
                picket.get().experimentalData().mn_2(),
                newAmperage,
                picket.get().experimentalData().voltage(),
                picket.get().experimentalData().resistanceApparent(),
                picket.get().experimentalData().errorResistanceApparent(),
                picket.get().experimentalData().polarizationApparent(),
                picket.get().experimentalData().errorPolarizationApparent()
        );
        if (!event.getNewValue().isNaN()) {
            setIfValidElseAlert(newExpData);
        } else {
            table.refresh();
        }
    }

    @FXML
    private void onVoltageEditCommit(TableColumn.CellEditEvent<ExperimentalDataRow, Double> event) {
        int index = event.getRowValue().index();
        List<Double> newVoltage = new ArrayList<>(picket.get().experimentalData().voltage());
        newVoltage.set(index, event.getNewValue());
        ExperimentalData newExpData = new ExperimentalData(
                picket.get().experimentalData().ab_2(),
                picket.get().experimentalData().mn_2(),
                picket.get().experimentalData().amperage(),
                newVoltage,
                picket.get().experimentalData().resistanceApparent(),
                picket.get().experimentalData().errorResistanceApparent(),
                picket.get().experimentalData().polarizationApparent(),
                picket.get().experimentalData().errorPolarizationApparent()
        );
        if (!event.getNewValue().isNaN()) {
            setIfValidElseAlert(newExpData);
        } else {
            table.refresh();
        }
    }

    @FXML
    private void onPolarizationApparentEditCommit(TableColumn.CellEditEvent<ExperimentalDataRow, Double> event) {
        int index = event.getRowValue().index();
        List<Double> newPolApp = new ArrayList<>(picket.get().experimentalData().polarizationApparent());
        newPolApp.set(index, event.getNewValue());
        ExperimentalData newExpData = new ExperimentalData(
                picket.get().experimentalData().ab_2(),
                picket.get().experimentalData().mn_2(),
                picket.get().experimentalData().amperage(),
                picket.get().experimentalData().voltage(),
                picket.get().experimentalData().resistanceApparent(),
                picket.get().experimentalData().errorResistanceApparent(),
                newPolApp,
                picket.get().experimentalData().errorPolarizationApparent()
        );
        if (!event.getNewValue().isNaN()) {
            setIfValidElseAlert(newExpData);
        } else {
            table.refresh();
        }
    }

    @FXML
    private void onErrorPolarizationApparentEditCommit(TableColumn.CellEditEvent<ExperimentalDataRow, Double> event) {
        int index = event.getRowValue().index();
        List<Double> newErrorPolApp = new ArrayList<>(picket.get().experimentalData().errorPolarizationApparent());
        newErrorPolApp.set(index, event.getNewValue());
        ExperimentalData newExpData = new ExperimentalData(
                picket.get().experimentalData().ab_2(),
                picket.get().experimentalData().mn_2(),
                picket.get().experimentalData().amperage(),
                picket.get().experimentalData().voltage(),
                picket.get().experimentalData().resistanceApparent(),
                picket.get().experimentalData().errorResistanceApparent(),
                picket.get().experimentalData().polarizationApparent(),
                newErrorPolApp
        );
        if (!event.getNewValue().isNaN()) {
            setIfValidElseAlert(newExpData);
        } else {
            table.refresh();
        }
    }
}

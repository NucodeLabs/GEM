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
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.app.model.SectionManager;
import ru.nucodelabs.gem.app.snapshot.HistoryManager;
import ru.nucodelabs.gem.view.AlertsFactory;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import static java.lang.Math.min;

public class ExperimentalTableController extends AbstractEditableTableController {

    private final ObservableObjectValue<Picket> picket;

    @FXML
    private TableColumn<Object, Integer> indexCol;
    @FXML
    private TableColumn<ExperimentalData, Double> ab2Col;
    @FXML
    private TableColumn<ExperimentalData, Double> mn2Col;
    @FXML
    private TableColumn<ExperimentalData, Double> resistanceApparentCol;
    @FXML
    private TableColumn<ExperimentalData, Double> errorResistanceCol;
    @FXML
    private TableColumn<ExperimentalData, Double> amperageCol;
    @FXML
    private TableColumn<ExperimentalData, Double> voltageCol;
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
    private TableView<ExperimentalData> table;

    private List<TextField> requiredForAdd;

    @Inject
    private Validator validator;
    @Inject
    private IntegerProperty picketIndex;
    @Inject
    private SectionManager sectionManager;
    @Inject
    private HistoryManager<Section> historyManager;
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
                .addListener((ListChangeListener<? super ExperimentalData>) c -> {
                    if (c.next()) {
                        deleteBtn.setDisable(c.getList().isEmpty());
                    }
                });

        indexCol.setCellFactory(Tables.indexCellFactory());

        ab2Col.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue().getAb2()));
        mn2Col.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue().getMn2()));
        resistanceApparentCol.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue().getResistanceApparent()));
        errorResistanceCol.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue().getErrorResistanceApparent()));
        amperageCol.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue().getAmperage()));
        voltageCol.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue().getVoltage()));

        for (int i = 1; i < table.getColumns().size(); i++) {
            // safe cast
            ((TableColumn<ExperimentalData, Double>) table.getColumns().get(i))
                    .setCellFactory(TextFieldTableCell.forTableColumn(Tables.doubleStringConverter()));
        }

        requiredForAdd.forEach(textField -> addValidationListener(textField, Tables::validateDoubleInput));
        requiredForAdd.forEach(this::addEnterKeyHandler);
        addValidationListener(indexTextField, Tables::validateIndexInput);

        table.itemsProperty().addListener((observable, oldValue, newValue) -> {
            newValue.addListener((ListChangeListener<? super ExperimentalData>) c -> table.refresh());
            table.refresh();
        });
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
        List<ExperimentalData> newExpData = deleteIndices(
                table.getSelectionModel().getSelectedIndices(),
                picket.get().getExperimentalData());
        updateIfValidElseAlert(newExpData);
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

            int index = picket.get().getExperimentalData().size();

            try {
                index = Integer.parseInt(indexTextField.getText());
            } catch (NumberFormatException ignored) {
            }

            index = min(index, picket.get().getExperimentalData().size());

            List<ExperimentalData> experimentalData = new ArrayList<>(picket.get().getExperimentalData());

            experimentalData.add(index, ExperimentalData.create(
                    newAb2Value, newMn2Value, newResAppValue, newErrResAppValue, newAmperageValue, newVoltageValue
            ));

            updateIfValidElseAlert(experimentalData);
        }
    }


    private void updateIfValidElseAlert(List<ExperimentalData> newExpData) {
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
    private void onEditCommit(TableColumn.CellEditEvent<ExperimentalData, Double> event) {
        int index = event.getTablePosition().getRow();

        double newInputValue = event.getNewValue();
        ExperimentalData oldValue = event.getRowValue();
        ExperimentalData newValue;
        var column = event.getTableColumn();
        if (column == ab2Col) {
            newValue = ExperimentalData.create(
                    newInputValue,
                    oldValue.getMn2(),
                    oldValue.getResistanceApparent(),
                    oldValue.getErrorResistanceApparent(),
                    oldValue.getAmperage(),
                    oldValue.getVoltage()
            );
        } else if (column == mn2Col) {
            newValue = ExperimentalData.create(
                    oldValue.getAb2(),
                    newInputValue,
                    oldValue.getResistanceApparent(),
                    oldValue.getErrorResistanceApparent(),
                    oldValue.getAmperage(),
                    oldValue.getVoltage()
            );
        } else if (column == resistanceApparentCol) {
            newValue = ExperimentalData.create(
                    oldValue.getAb2(),
                    oldValue.getMn2(),
                    newInputValue,
                    oldValue.getErrorResistanceApparent(),
                    oldValue.getAmperage(),
                    oldValue.getVoltage()
            );
        } else if (column == errorResistanceCol) {
            newValue = ExperimentalData.create(
                    oldValue.getAb2(),
                    oldValue.getMn2(),
                    oldValue.getResistanceApparent(),
                    newInputValue,
                    oldValue.getAmperage(),
                    oldValue.getVoltage()
            );
        } else if (column == amperageCol) {
            newValue = ExperimentalData.create(
                    oldValue.getAb2(),
                    oldValue.getMn2(),
                    oldValue.getResistanceApparent(),
                    oldValue.getErrorResistanceApparent(),
                    newInputValue,
                    oldValue.getVoltage()
            );
        } else if (column == voltageCol) {
            newValue = ExperimentalData.create(
                    oldValue.getAb2(),
                    oldValue.getMn2(),
                    oldValue.getResistanceApparent(),
                    oldValue.getErrorResistanceApparent(),
                    oldValue.getAmperage(),
                    newInputValue
            );
        } else {
            throw new RuntimeException("Something went wrong!");
        }

        List<ExperimentalData> newExpData = new ArrayList<>(picket.get().getExperimentalData());

        newExpData.set(index, newValue);

        if (!event.getNewValue().isNaN()) {
            updateIfValidElseAlert(newExpData);
        } else {
            table.refresh();
        }
    }
}

package ru.nucodelabs.gem.view.tables;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.app.model.SectionManager;
import ru.nucodelabs.gem.app.snapshot.HistoryManager;
import ru.nucodelabs.gem.utils.FXUtils;
import ru.nucodelabs.gem.view.AbstractController;
import ru.nucodelabs.gem.view.AlertsFactory;

import javax.inject.Inject;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;

import static java.lang.Math.min;

public class ExperimentalTableController extends AbstractController {

    private final ObservableObjectValue<Picket> picket;
    @FXML
    private Button recalculateBtn;

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

    @Inject
    private Validator validator;
    @Inject
    private SectionManager sectionManager;
    @Inject
    private HistoryManager<Section> historyManager;
    @Inject
    private AlertsFactory alertsFactory;
    @Inject
    private StringConverter<Double> doubleStringConverter;
    @Inject
    private DecimalFormat decimalFormat;

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

        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.getSelectionModel().getSelectedItems()
                .addListener((ListChangeListener<? super ExperimentalData>) c -> {
                    if (c.next()) {
                        deleteBtn.setDisable(c.getList().isEmpty());
                        recalculateBtn.setDisable(c.getList().isEmpty());
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
                    .setCellFactory(TextFieldTableCell.forTableColumn(doubleStringConverter));
        }

        Predicate<String> validateDataInput = s -> Tables.validateDoubleInput(s, decimalFormat);
        BooleanBinding validInput
                = Tables.setupInputValidation(ab2TextField, validateDataInput)
                .and(Tables.setupInputValidation(mn2TextField, validateDataInput))
                .and(Tables.setupInputValidation(resAppTextField, validateDataInput))
                .and(Tables.setupInputValidation(errResAppTextField, validateDataInput))
                .and(Tables.setupInputValidation(voltageTextField, validateDataInput))
                .and(Tables.setupInputValidation(amperageTextField, validateDataInput))
                .and(Tables.setupInputValidation(indexTextField, Tables::validateIndexInput));

        BooleanBinding allRequiredNotBlank
                = FXUtils.isNotBlank(ab2TextField.textProperty())
                .and(FXUtils.isNotBlank(mn2TextField.textProperty()))
                .and(FXUtils.isNotBlank(resAppTextField.textProperty()))
                .and(FXUtils.isNotBlank(errResAppTextField.textProperty()))
                .and(FXUtils.isNotBlank(voltageTextField.textProperty()))
                .and(FXUtils.isNotBlank(amperageTextField.textProperty()));

        addBtn.disableProperty().bind(validInput.not().or(allRequiredNotBlank.not()));

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
        table.refresh();
    }


    @FXML
    private void deleteSelected() {
        List<ExperimentalData> newExpData = Tables.deleteIndices(
                table.getSelectionModel().getSelectedIndices(),
                picket.get().getExperimentalData());
        updateIfValidElseAlert(newExpData);
    }

    @FXML
    private void add() {
        if (!addBtn.isDisable()) {

            double newAb2Value;
            double newMn2Value;
            double newResAppValue;
            double newErrResAppValue;
            double newAmperageValue;
            double newVoltageValue;
            try {
                newAb2Value = decimalFormat.parse(ab2TextField.getText()).doubleValue();
                newMn2Value = decimalFormat.parse(mn2TextField.getText()).doubleValue();
                newResAppValue = decimalFormat.parse(resAppTextField.getText()).doubleValue();
                newErrResAppValue = decimalFormat.parse(errResAppTextField.getText()).doubleValue();
                newAmperageValue = decimalFormat.parse(amperageTextField.getText()).doubleValue();
                newVoltageValue = decimalFormat.parse(voltageTextField.getText()).doubleValue();
            } catch (ParseException e) {
                return;
            }

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
        Picket modified = picket.get().withExperimentalData(newExpData);

        Set<ConstraintViolation<Picket>> violations = validator.validate(modified);

        if (!violations.isEmpty()) {
            alertsFactory.violationsAlert(violations, getStage()).show();
            table.refresh();
        } else {
            historyManager.performThenSnapshot(
                    () -> sectionManager.update(modified));
            FXUtils.unfocus(
                    indexTextField,
                    ab2TextField,
                    mn2TextField,
                    resAppTextField,
                    errResAppTextField,
                    amperageTextField,
                    voltageTextField);
        }
    }

    @FXML
    private void onEditCommit(TableColumn.CellEditEvent<ExperimentalData, Double> event) {
        int index = event.getTablePosition().getRow();

        double newInputValue = event.getNewValue();
        ExperimentalData oldValue = event.getRowValue();
        ExperimentalData newValue;
        var column = event.getTableColumn();
        if (column == ab2Col) {
            newValue = oldValue.withAb2(newInputValue);
        } else if (column == mn2Col) {
            newValue = oldValue.withMn2(newInputValue);
        } else if (column == resistanceApparentCol) {
            newValue = oldValue.withResistanceApparent(newInputValue);
        } else if (column == errorResistanceCol) {
            newValue = oldValue.withErrorResistanceApparent(newInputValue);
        } else if (column == amperageCol) {
            newValue = oldValue.withAmperage(newInputValue);
        } else if (column == voltageCol) {
            newValue = oldValue.withVoltage(newInputValue);
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

    @FXML
    private void recalculateSelected() {
        List<ExperimentalData> experimentalData = new ArrayList<>(picket.get().getExperimentalData());

        List<Integer> ind = table.getSelectionModel().getSelectedIndices();

        for (int i = 0; i < experimentalData.size(); i++) {
            if (ind.contains(i)) {
                experimentalData.set(i, experimentalData.get(i).recalculateResistanceApparent());
            }
        }

        historyManager.performThenSnapshot(() -> sectionManager.update(picket.get().withExperimentalData(experimentalData)));
    }
}

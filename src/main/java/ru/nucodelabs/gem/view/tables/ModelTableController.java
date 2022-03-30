package ru.nucodelabs.gem.view.tables;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.ModelDataRow;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.app.AppService;
import ru.nucodelabs.gem.app.command.PicketModificationCommand;
import ru.nucodelabs.gem.view.AbstractController;
import ru.nucodelabs.gem.view.AlertsFactory;

import javax.inject.Inject;
import java.net.URL;
import java.util.*;

import static ru.nucodelabs.gem.view.tables.Tables.validateDataInput;
import static ru.nucodelabs.gem.view.tables.Tables.validateIndexInput;

public class ModelTableController extends AbstractController {

    private final ObjectProperty<Picket> picket;
    @FXML
    public TextField powerTextField;
    @FXML
    public TextField resistanceTextField;
    @FXML
    public TextField polarizationTextField;
    @FXML
    public TextField indexTextField;
    @FXML
    public Button deleteBtn;
    @FXML
    public Button addBtn;
    @FXML
    private TableView<ModelDataRow> table;
    @Inject
    private AppService appService;
    @Inject
    private AlertsFactory alertsFactory;
    @Inject
    private Validator validator;
    @Inject
    private PicketModificationCommand.Factory commandFactory;

    private List<TextField> requiredForAdd;

    @Inject
    public ModelTableController(ObjectProperty<Picket> picket) {
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

        indexTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            indexTextField.getStyleClass().remove("wrong-input");
            addBtn.setDisable(false);
            if (!validateIndexInput(newValue)) {
                indexTextField.getStyleClass().add("wrong-input");
                addBtn.setDisable(true);
            } else {
                if (!requiredForAdd.stream()
                        .allMatch(textField ->
                                !textField.getText().isBlank()
                                        && validateDataInput(textField.getText()))) {
                    addBtn.setDisable(true);
                }
            }
        });

        requiredForAdd = List.of(powerTextField, resistanceTextField, polarizationTextField);
        addInputCheckListener(polarizationTextField);
        addInputCheckListener(resistanceTextField);
        addInputCheckListener(powerTextField);
    }

    private void addInputCheckListener(TextField doubleTextField) {
        doubleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            doubleTextField.getStyleClass().remove("wrong-input");
            addBtn.setDisable(false);
            if (!validateDataInput(newValue)) {
                doubleTextField.getStyleClass().add("wrong-input");
                addBtn.setDisable(true);
            } else {
                if (!requiredForAdd.stream()
                        .allMatch(textField ->
                                !textField.getText().isBlank()
                                        && validateDataInput(textField.getText()))) {
                    addBtn.setDisable(true);
                }
            }
        });
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
    public void addLayer() {
        if (!resistanceTextField.getText().isBlank()
                && !powerTextField.getText().isBlank()
                && !polarizationTextField.getText().isBlank()) {

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

            if (!indexTextField.getText().isBlank()) {
                int index;
                try {
                    index = Integer.parseInt(indexTextField.getText());
                } catch (NumberFormatException e) {
                    return;
                }
                try {
                    newResistance.add(index, newResistanceValue);
                    newPower.add(index, newPowerValue);
                    newPolarization.add(index, newPolarizationValue);
                } catch (IndexOutOfBoundsException e) {
                    newResistance.add(newResistanceValue);
                    newPower.add(newPowerValue);
                    newPolarization.add(newPolarizationValue);
                }
            } else {
                newResistance.add(newResistanceValue);
                newPower.add(newPowerValue);
                newPolarization.add(newPolarizationValue);
            }

            ModelData newModelData = new ModelData(
                    newResistance,
                    newPolarization,
                    newPower
            );

            setIfValidElseAlert(newModelData);
        }
    }

    @FXML
    public void deleteSelected() {
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
            appService.execute(
                    commandFactory.create(
                            new Picket(
                                    picket.get().name(),
                                    picket.get().experimentalData(),
                                    newModelData
                            )
                    )
            );
        }
    }

    @FXML
    private void importModel(Event event) {
        appService.importMOD();
    }
}

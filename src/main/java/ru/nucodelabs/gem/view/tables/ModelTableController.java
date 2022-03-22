package ru.nucodelabs.gem.view.tables;

import com.google.inject.name.Named;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.ModelTableLine;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.convert.VESTablesConverters;

import javax.inject.Inject;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ModelTableController extends Controller {

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
    private TableView<ModelTableLine> table;
    @Inject
    @Named("ImportMOD")
    private Runnable importMOD;

    private List<TextField> requiredForAdd;

    @Inject
    public ModelTableController(
            ObjectProperty<Picket> picket) {
        this.picket = picket;

        picket.addListener((observable, oldValue, newValue) -> {
            if (oldValue == null
                    || oldValue.modelData() == null
                    || !oldValue.modelData().equals(newValue.modelData())) {
                update();
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.getSelectionModel().getSelectedItems()
                .addListener((ListChangeListener<? super ModelTableLine>) c -> {
                    if (c.next()) {
                        deleteBtn.setDisable(c.getList().isEmpty());
                    }
                });
        for (int i = 1; i < table.getColumns().size(); i++) {
            // safe cast
            ((TableColumn<ModelTableLine, Double>) table.getColumns().get(i))
                    .setCellFactory(TextFieldTableCell.forTableColumn(Tables.doubleStringConverter));
        }

        indexTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            indexTextField.getStyleClass().remove("wrong-input");
            addBtn.setDisable(false);
            if (!validateIndexInput(newValue)) {
                indexTextField.getStyleClass().add("wrong-input");
                addBtn.setDisable(true);
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

    private boolean validateIndexInput(String s) {
        if (s.isBlank()) {
            return true;
        }
        try {
            int val = Integer.parseInt(s);
            return val >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean validateDataInput(String s) {
        if (s.isBlank()) {
            return true;
        }
        try {
            double val = Double.parseDouble(s);
            return !(val < 0);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    protected Stage getStage() {
        return (Stage) table.getScene().getWindow();
    }

    protected void update() {
        ObservableList<ModelTableLine> modelTableLines = FXCollections.emptyObservableList();

        if (picket.get().modelData() != null) {
            modelTableLines = VESTablesConverters.toModelTableData(
                    picket.get().modelData()
            );
        }

        table.itemsProperty().setValue(modelTableLines);
    }

    @FXML
    private void onPowerEditCommit(TableColumn.CellEditEvent<ModelTableLine, Double> event) {
        int index = event.getRowValue().index();
        List<Double> newPower = new ArrayList<>(picket.get().modelData().power());
        newPower.set(index, event.getNewValue());
        ModelData newModelData = new ModelData(
                picket.get().modelData().resistance(),
                picket.get().modelData().polarization(),
                newPower
        );
        if (invalidInputAlert(newModelData) || event.getNewValue().isNaN()) {
            table.refresh();
            return;
        }
        picket.set(
                new Picket(
                        picket.get().name(),
                        picket.get().experimentalData(),
                        newModelData
                )
        );
    }

    @FXML
    private void onResistanceEditCommit(TableColumn.CellEditEvent<ModelTableLine, Double> event) {
        int index = event.getRowValue().index();
        List<Double> newResistance = new ArrayList<>(picket.get().modelData().resistance());
        newResistance.set(index, event.getNewValue());
        ModelData newModelData = new ModelData(
                newResistance,
                picket.get().modelData().polarization(),
                picket.get().modelData().power()
        );
        if (invalidInputAlert(newModelData) || event.getNewValue().isNaN()) {
            table.refresh();
            return;
        }
        picket.set(
                new Picket(
                        picket.get().name(),
                        picket.get().experimentalData(),
                        newModelData
                )
        );
    }

    @FXML
    private void onPolarizationEditCommit(TableColumn.CellEditEvent<ModelTableLine, Double> event) {
        int index = event.getRowValue().index();
        List<Double> newPolarization = new ArrayList<>(picket.get().modelData().resistance());
        newPolarization.set(index, event.getNewValue());
        ModelData newModelData = new ModelData(
                picket.get().modelData().resistance(),
                newPolarization,
                picket.get().modelData().power()
        );
        if (invalidInputAlert(newModelData) || event.getNewValue().isNaN()) {
            table.refresh();
            return;
        }
        picket.set(
                new Picket(
                        picket.get().name(),
                        picket.get().experimentalData(),
                        newModelData
                )
        );
    }

    private boolean invalidInputAlert(ModelData modelData) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<ModelData>> violations = validator.validate(modelData);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("\n"));
            Alert alert = new Alert(Alert.AlertType.ERROR, message);
            alert.initOwner(getStage());
            alert.show();
            return true;
        } else {
            return false;
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
            if (!invalidInputAlert(newModelData)) {
                picket.set(new Picket(
                        picket.get().name(),
                        picket.get().experimentalData(),
                        newModelData
                ));
            }
        }
    }

    @FXML
    public void deleteSelected() {
        List<ModelTableLine> selectedRows = table.getSelectionModel().getSelectedItems();
        List<Double> newResistance = new ArrayList<>(picket.get().modelData().resistance());
        List<Double> newPower = new ArrayList<>(picket.get().modelData().power());
        List<Double> newPolarization = new ArrayList<>(picket.get().modelData().polarization());

        List<Integer> indicesToRemove = selectedRows.stream()
                .map(ModelTableLine::index)
                .sorted(Collections.reverseOrder())
                .toList();

        indicesToRemove.forEach(i -> {
            int index = i;
            newResistance.remove(index);
            newPolarization.remove(index);
            newPower.remove(index);
        });

        picket.set(new Picket(
                picket.get().name(),
                picket.get().experimentalData(),
                new ModelData(newResistance, newPolarization, newPower)
        ));
    }

    public void importModel() {
        importMOD.run();
    }
}

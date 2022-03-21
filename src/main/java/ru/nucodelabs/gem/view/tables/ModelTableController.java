package ru.nucodelabs.gem.view.tables;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.ModelTableLine;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.convert.VESTablesConverters;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
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
    private TableView<ModelTableLine> table;

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
        for (int i = 1; i < table.getColumns().size(); i++) {
            // safe cast
            ((TableColumn<ModelTableLine, Double>) table.getColumns().get(i))
                    .setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        }

        polarizationTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            polarizationTextField.getStyleClass().remove("wrong-input");
            try {
                Double.parseDouble(newValue);
            } catch (NumberFormatException e) {
                if (!newValue.isBlank()) {
                    polarizationTextField.getStyleClass().add("wrong-input");
                }
            }
        });
        resistanceTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            resistanceTextField.getStyleClass().remove("wrong-input");
            try {
                Double.parseDouble(newValue);
            } catch (NumberFormatException e) {
                if (!newValue.isBlank()) {
                    resistanceTextField.getStyleClass().add("wrong-input");
                }
            }
        });
        powerTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            powerTextField.getStyleClass().remove("wrong-input");
            try {
                Double.parseDouble(newValue);
            } catch (NumberFormatException e) {
                if (!newValue.isBlank()) {
                    powerTextField.getStyleClass().add("wrong-input");
                }
            }
        });
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
        if (invalidInputAlert(newModelData)) {
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
        if (invalidInputAlert(newModelData)) {
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
        if (invalidInputAlert(newModelData)) {
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
            String message = violations.stream().map(v -> v.getPropertyPath() + " " + v.getMessage()).collect(Collectors.joining("\n"));
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
            double newResistanceValue;
            double newPowerValue;
            double newPolarizationValue;
            try {
                newResistanceValue = Double.parseDouble(resistanceTextField.getText());
                newPowerValue = Double.parseDouble(powerTextField.getText());
                newPolarizationValue = Double.parseDouble(polarizationTextField.getText());
            } catch (NumberFormatException e) {
                return;
            }

            List<Double> newResistance = new ArrayList<>(picket.get().modelData().resistance());
            newResistance.add(newResistanceValue);
            List<Double> newPower = new ArrayList<>(picket.get().modelData().power());
            newPower.add(newPowerValue);
            List<Double> newPolarization = new ArrayList<>(picket.get().modelData().polarization());
            newPolarization.add(newPolarizationValue);
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
}

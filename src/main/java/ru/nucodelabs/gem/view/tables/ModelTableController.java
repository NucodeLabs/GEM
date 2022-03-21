package ru.nucodelabs.gem.view.tables;

import com.google.inject.name.Named;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import ru.nucodelabs.data.ves.ModelTableLine;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.convert.VESTablesConverters;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class ModelTableController extends Controller {

    private final ObjectProperty<Picket> picket;
    @Inject
    @Named("VESCurves")
    private ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> dataProperty;

    @FXML
    private TableView<ModelTableLine> table;

    @FXML
    private TableColumn<ModelTableLine, Double> powerCol;

    @FXML
    private TableColumn<ModelTableLine, Double> resistanceCol;

    @FXML
    private TableColumn<ModelTableLine, Double> polarizationCol;

    @Inject
    public ModelTableController(ObjectProperty<Picket> picket) {
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
        //table.itemsProperty().bindBidirectional(dataProperty);
        setupColumns();
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

    protected void setupColumns() {
        setupPowerColumn();
        setupResistanceColumn();
        setupPolarizationColumn();
    }

    protected void setupPowerColumn() {
        setupColumn(powerCol, 1);
    }

    protected void setupResistanceColumn() {
        setupColumn(resistanceCol, 2);
    }

    protected void setupPolarizationColumn() {
        setupColumn(polarizationCol, 3);
    }

    private void setupColumn(TableColumn<ModelTableLine, Double> column, int characteristic) {
        column.setCellFactory(ModelTableCell.forTableColumn(new DoubleStringConverter()));

        // updates the column field on the ModelTableLine object to the committed value
        column.setOnEditCommit(event -> {
            final Double value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            switch (characteristic) {
                case 1 -> event.getTableView().getItems().get(event.getTablePosition().getRow()).setPower(value);
                case 2 -> event.getTableView().getItems().get(event.getTablePosition().getRow()).setResistance(value);
                case 3 -> event.getTableView().getItems().get(event.getTablePosition().getRow()).setPolarization(value);
            }

            table.refresh();
        });
    }
}

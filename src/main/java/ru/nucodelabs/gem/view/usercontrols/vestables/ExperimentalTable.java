package ru.nucodelabs.gem.view.usercontrols.vestables;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.nucodelabs.gem.view.usercontrols.vestables.tablelines.ExperimentalTableLine;
import ru.nucodelabs.mvvm.VBUserControl;

public class ExperimentalTable extends VBUserControl {
    @FXML
    TableView<ExperimentalTableLine> experimentalTable;

    @FXML
    TableColumn<ExperimentalTableLine, Double> expAB_2Column;
    @FXML
    TableColumn<ExperimentalTableLine, Double> expMN_2Column;
    @FXML
    TableColumn<ExperimentalTableLine, Double> expResistanceColumn;
    @FXML
    TableColumn<ExperimentalTableLine, Double> expResistanceErrorColumn;
    @FXML
    TableColumn<ExperimentalTableLine, Double> expPolarisationColumn;
    @FXML
    TableColumn<ExperimentalTableLine, Double> expPolarisationErrorColumn;
    @FXML
    TableColumn<ExperimentalTableLine, Double> expAmperageColumn;
    @FXML
    TableColumn<ExperimentalTableLine, Double> expVoltageColumn;

    public ExperimentalTable() {
        super();
        experimentalTable = new TableView<>();

        expAB_2Column = new TableColumn<>("%");
        expAB_2Column.setCellValueFactory(new PropertyValueFactory<>("expAB_2"));

        expMN_2Column = new TableColumn<>();
        expMN_2Column.setCellValueFactory(new PropertyValueFactory<>("expMN_2"));

        expResistanceColumn = new TableColumn<>();
        expResistanceColumn.setCellValueFactory(new PropertyValueFactory<>("expResistance"));

        expResistanceErrorColumn = new TableColumn<>();
        expResistanceErrorColumn.setCellValueFactory(new PropertyValueFactory<>("expErrorResistance"));

        expPolarisationColumn = new TableColumn<>();
        expPolarisationColumn.setCellValueFactory(new PropertyValueFactory<>("expPolarisation"));

        expPolarisationErrorColumn = new TableColumn<>();
        expPolarisationErrorColumn.setCellValueFactory(new PropertyValueFactory<>("expErrorPolarisation"));

        expAmperageColumn = new TableColumn<>();
        expAmperageColumn.setCellValueFactory(new PropertyValueFactory<>("expAmperage"));

        expVoltageColumn = new TableColumn<>();
        expVoltageColumn.setCellValueFactory(new PropertyValueFactory<>("expVoltage"));

        experimentalTable.getColumns().addAll(
                expAB_2Column,
                expMN_2Column,
                expResistanceColumn,
                expResistanceErrorColumn,
                expPolarisationColumn,
                expPolarisationErrorColumn,
                expAmperageColumn,
                expVoltageColumn
        );
    }

    public TableView<ExperimentalTableLine> getExperimentalTable() {
        return experimentalTable;
    }

    public TableColumn<ExperimentalTableLine, Double> getExpAB_2Column() {
        return expAB_2Column;
    }

    public TableColumn<ExperimentalTableLine, Double> getExpMN_2Column() {
        return expMN_2Column;
    }

    public TableColumn<ExperimentalTableLine, Double> getExpResistanceColumn() {
        return expResistanceColumn;
    }

    public TableColumn<ExperimentalTableLine, Double> getExpResistanceErrorColumn() {
        return expResistanceErrorColumn;
    }

    public TableColumn<ExperimentalTableLine, Double> getExpPolarisationColumn() {
        return expPolarisationColumn;
    }

    public TableColumn<ExperimentalTableLine, Double> getExpPolarisationErrorColumn() {
        return expPolarisationErrorColumn;
    }

    public TableColumn<ExperimentalTableLine, Double> getExpAmperageColumn() {
        return expAmperageColumn;
    }

    public TableColumn<ExperimentalTableLine, Double> getExpVoltageColumn() {
        return expVoltageColumn;
    }
}

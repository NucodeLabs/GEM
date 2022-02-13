package ru.nucodelabs.gem.view.usercontrols.vestables;

import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.nucodelabs.gem.view.usercontrols.vestables.property_data.ExpTableLine;
import ru.nucodelabs.mvvm.VBUserControl;

public class EXPTable extends VBUserControl {
    @FXML
    TableView<ExpTableLine> experimentalTable;

    @FXML
    TableColumn<ExpTableLine, Double> expAB_2Column;
    @FXML
    TableColumn<ExpTableLine, Double> expMN_2Column;
    @FXML
    TableColumn<ExpTableLine, Double> expResistanceColumn;
    @FXML
    TableColumn<ExpTableLine, Double> expResistanceErrorColumn;
    @FXML
    TableColumn<ExpTableLine, Double> expPolarisationColumn;
    @FXML
    TableColumn<ExpTableLine, Double> expPolarisationErrorColumn;
    @FXML
    TableColumn<ExpTableLine, Double> expAmperageColumn;
    @FXML
    TableColumn<ExpTableLine, Double> expVoltageColumn;

    public EXPTable() {
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
                expVoltageColumn);
    }

    public TableView<ExpTableLine> getExperimentalTable() {
        return experimentalTable;
    }

    public TableColumn<ExpTableLine, Double> getExpAB_2Column() {
        return expAB_2Column;
    }

    public TableColumn<ExpTableLine, Double> getExpMN_2Column() {
        return expMN_2Column;
    }

    public TableColumn<ExpTableLine, Double> getExpResistanceColumn() {
        return expResistanceColumn;
    }

    public TableColumn<ExpTableLine, Double> getExpResistanceErrorColumn() {
        return expResistanceErrorColumn;
    }

    public TableColumn<ExpTableLine, Double> getExpPolarisationColumn() {
        return expPolarisationColumn;
    }

    public TableColumn<ExpTableLine, Double> getExpPolarisationErrorColumn() {
        return expPolarisationErrorColumn;
    }

    public TableColumn<ExpTableLine, Double> getExpAmperageColumn() {
        return expAmperageColumn;
    }

    public TableColumn<ExpTableLine, Double> getExpVoltageColumn() {
        return expVoltageColumn;
    }
}

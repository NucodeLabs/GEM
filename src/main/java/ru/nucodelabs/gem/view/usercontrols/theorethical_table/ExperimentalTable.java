package ru.nucodelabs.gem.view.usercontrols.theorethical_table;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ExperimentalTable {
    @FXML
    private TableView<SimpleDoubleProperty> experimentalTable;

    public TableView<SimpleDoubleProperty> getExperimentalTable() {
        return experimentalTable;
    }

    public void setExperimentalTable(TableView<SimpleDoubleProperty> experimentalTable) {
        this.experimentalTable = experimentalTable;
    }

    @FXML
    private TableColumn<SimpleDoubleProperty, SimpleDoubleProperty> expAB_2Column;

    public TableColumn<SimpleDoubleProperty, SimpleDoubleProperty> getExpAB_2Column() {
        return expAB_2Column;
    }

    public void setExpAB_2Column(TableColumn<SimpleDoubleProperty, SimpleDoubleProperty> expAB_2Column) {
        this.expAB_2Column = expAB_2Column;
    }

    @FXML
    private TableColumn<SimpleDoubleProperty, SimpleDoubleProperty> expMN_2Column;

    public TableColumn<SimpleDoubleProperty, SimpleDoubleProperty> getExpMN_2Column() {
        return expMN_2Column;
    }

    public void setExpMN_2Column(TableColumn<SimpleDoubleProperty, SimpleDoubleProperty> expMN_2Column) {
        this.expMN_2Column = expMN_2Column;
    }

    @FXML
    private TableColumn<SimpleDoubleProperty, SimpleDoubleProperty> expResistanceColumn;

    public TableColumn<SimpleDoubleProperty, SimpleDoubleProperty> getExpResistanceColumn() {
        return expResistanceColumn;
    }

    public void setExpResistanceColumn(TableColumn<SimpleDoubleProperty, SimpleDoubleProperty> expResistanceColumn) {
        this.expResistanceColumn = expResistanceColumn;
    }

    @FXML
    private TableColumn<SimpleDoubleProperty, SimpleDoubleProperty> expResistanceErrorColumn;

    public TableColumn<SimpleDoubleProperty, SimpleDoubleProperty> getExpResistanceErrorColumn() {
        return expResistanceErrorColumn;
    }

    public void setExpResistanceErrorColumn(TableColumn<SimpleDoubleProperty, SimpleDoubleProperty> expResistanceErrorColumn) {
        this.expResistanceErrorColumn = expResistanceErrorColumn;
    }

    @FXML
    private TableColumn<SimpleDoubleProperty, SimpleDoubleProperty> expAmperageColumn;

    public TableColumn<SimpleDoubleProperty, SimpleDoubleProperty> getExpAmperageColumn() {
        return expAmperageColumn;
    }

    public void setExpAmperageColumn(TableColumn<SimpleDoubleProperty, SimpleDoubleProperty> expAmperageColumn) {
        this.expAmperageColumn = expAmperageColumn;
    }

    @FXML
    private TableColumn<SimpleDoubleProperty, SimpleDoubleProperty> expVoltageColumn;

    public TableColumn<SimpleDoubleProperty, SimpleDoubleProperty> getExpVoltageColumn() {
        return expVoltageColumn;
    }

    public void setExpVoltageColumn(TableColumn<SimpleDoubleProperty, SimpleDoubleProperty> expVoltageColumn) {
        this.expVoltageColumn = expVoltageColumn;
    }

    public ExperimentalTable() {
        this.experimentalTable = new TableView<>();
        
    }
    
    TableView<SimpleDoubleProperty, SimpleDoubleProperty>
    
}

package ru.nucodelabs.gem.view.usercontrols.tables.experimental;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.view.DataTableConverters;
import ru.nucodelabs.gem.view.usercontrols.tables.property_data.ExpTableLine;

public class ExperimentalTable {
    @FXML
    TableView<ExpTableLine> experimentalTable;

    private ExperimentalData data;
    private ObservableList<ExpTableLine> oTableLines;

    public ExperimentalTable() {
        this.experimentalTable = new TableView<>();
        this.data = new ExperimentalData();
    }

    public ExperimentalTable(Picket currentPicket) {
        this.experimentalTable = new TableView<>();
        this.data = currentPicket.getExperimentalData();

        this.oTableLines = DataTableConverters.expDataToExpObservableTableLines(data);
        this.experimentalTable.setItems(oTableLines);
    }

    public void setExpTableLines(ExperimentalData expData) {
        this.data = expData;
        setTable(data);
    }

    public void setExpTableLines(Picket currentPicket) {
        this.data = currentPicket.getExperimentalData();
        setTable(data);
    }

    private void setTable(ExperimentalData data) {
        this.oTableLines = DataTableConverters.expDataToExpObservableTableLines(data);
        this.experimentalTable.setItems(oTableLines);
    }

    public TableView<ExpTableLine> getExperimentalTable() {
        return experimentalTable;
    }

    public void setExperimentalTable(TableView<ExpTableLine> experimentalTable) {
        this.experimentalTable = experimentalTable;
    }

    public ExperimentalData getData() {
        return data;
    }

    public void setData(ExperimentalData data) {
        this.data = data;
    }

    public ObservableList<ExpTableLine> getoTableLines() {
        return oTableLines;
    }

    public void setoTableLines(ObservableList<ExpTableLine> oTableLines) {
        this.oTableLines = oTableLines;
    }
}

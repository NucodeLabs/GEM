package ru.nucodelabs.gem.view.usercontrols.tables.model;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.view.usercontrols.tables.property_data.ModelTableLine;

public class ModelTable {

    @FXML
    TableView<ModelTableLine> modelTable;

    private ModelData data;
    private ObservableList<ModelTableLine> oModelTableLines;

    public ModelTable() {
        this.modelTable = new TableView<>();
        this.data = new ModelData();
    }
}

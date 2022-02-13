package ru.nucodelabs.gem.view.usercontrols.vestables;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.view.usercontrols.vestables.property_data.ModelTableLine;
import ru.nucodelabs.mvvm.VBUserControl;

public class MODTable extends VBUserControl {

    @FXML
    TableView<ModelTableLine> modelTable;

    private ObservableList<ModelTableLine> oModelTableLines;

    public MODTable() {
        this.modelTable = new TableView<>();
    }
}

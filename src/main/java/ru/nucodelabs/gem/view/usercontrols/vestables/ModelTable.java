package ru.nucodelabs.gem.view.usercontrols.vestables;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import ru.nucodelabs.gem.view.usercontrols.vestables.tablelines.ModelTableLine;
import ru.nucodelabs.mvvm.VBUserControl;

public class ModelTable extends VBUserControl {

    @FXML
    TableView<ModelTableLine> modelTable;

    private ObservableList<ModelTableLine> oModelTableLines;

    public ModelTable() {
        this.modelTable = new TableView<>();
    }
}

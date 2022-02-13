package ru.nucodelabs.gem.view.usercontrols.vestables;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import ru.nucodelabs.gem.view.usercontrols.vestables.tablelines.ModelTableLine;
import ru.nucodelabs.mvvm.VBUserControl;

public class ModelTable extends VBUserControl {

    @FXML
    private TableView<ModelTableLine> modelTable;

    public TableView<ModelTableLine> getModelTable() {
        return modelTable;
    }
}

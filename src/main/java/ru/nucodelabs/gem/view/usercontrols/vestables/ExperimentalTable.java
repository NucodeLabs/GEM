package ru.nucodelabs.gem.view.usercontrols.vestables;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import ru.nucodelabs.gem.view.usercontrols.vestables.tablelines.ExperimentalTableLine;
import ru.nucodelabs.mvvm.VBUserControl;

public class ExperimentalTable extends VBUserControl {
    @FXML
    private TableView<ExperimentalTableLine> experimentalTable;

    public TableView<ExperimentalTableLine> getExperimentalTable() {
        return experimentalTable;
    }
}

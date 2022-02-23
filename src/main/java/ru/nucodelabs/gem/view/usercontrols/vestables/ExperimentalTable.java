package ru.nucodelabs.gem.view.usercontrols.vestables;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import ru.nucodelabs.gem.view.usercontrols.VBUserControl;
import ru.nucodelabs.gem.view.usercontrols.vestables.tablelines.ExperimentalTableLine;

public class ExperimentalTable extends VBUserControl {
    @FXML
    private TableView<ExperimentalTableLine> experimentalTable;

    public ObjectProperty<ObservableList<ExperimentalTableLine>> itemsProperty() {
        return experimentalTable.itemsProperty();
    }

    public ObservableList<ExperimentalTableLine> getItems() {
        return experimentalTable.getItems();
    }

    public void setItems(ObservableList<ExperimentalTableLine> value) {
        experimentalTable.setItems(value);
    }
}

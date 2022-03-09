package ru.nucodelabs.gem.view.usercontrols.vestables;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import ru.nucodelabs.gem.view.usercontrols.VBUserControl;
import ru.nucodelabs.gem.view.usercontrols.vestables.tablelines.ModelTableLine;

public class ModelTable extends VBUserControl {
    @FXML
    private TableView<ModelTableLine> modelTable;

    public ObjectProperty<ObservableList<ModelTableLine>> itemsProperty() {
        return modelTable.itemsProperty();
    }

    public ObservableList<ModelTableLine> getItems() {
        return modelTable.getItems();
    }

    public void setItems(ObservableList<ModelTableLine> value) {
        modelTable.setItems(value);
    }

    public void setPlaceholder(Node value) {
        modelTable.setPlaceholder(value);
    }

    public ObjectProperty<Node> placeholderProperty() {
        return modelTable.placeholderProperty();
    }

    public Node getPlaceholder() {
        return modelTable.getPlaceholder();
    }
}

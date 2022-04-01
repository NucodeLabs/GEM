package ru.nucodelabs.gem.view.main;

import javafx.beans.property.IntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.app.HistoryManager;
import ru.nucodelabs.gem.app.SectionManager;
import ru.nucodelabs.gem.view.AbstractController;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PicketsBarController extends AbstractController {

    private final ObservableList<Picket> picketObservableList;
    private final IntegerProperty picketIndex;

    @FXML
    public HBox container;

    @Inject
    private SectionManager sectionManager;
    @Inject
    private HistoryManager historyManager;

    @Inject
    public PicketsBarController(
            ObservableList<Picket> picketObservableList,
            IntegerProperty picketIndex) {

        this.picketObservableList = picketObservableList;
        this.picketIndex = picketIndex;
        picketObservableList.addListener((ListChangeListener<? super Picket>) c -> {
            if (c.next()) {
                if (c.getAddedSize() > 0 || c.getRemovedSize() > 0 || c.wasAdded()) {
                    update();
                }
            }
        });
        picketIndex.addListener((observable, oldValue, newValue) -> update());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    protected Stage getStage() {
        return (Stage) container.getScene().getWindow();
    }

    protected void update() {
        List<Button> buttons = new ArrayList<>();

        for (int i = 0; i < picketObservableList.size(); i++) {
            final int picketNumber = i;
            Button button = new Button(picketObservableList.get(picketNumber).name());

            if (i == picketIndex.get()) {
                button.setStyle(
                        "-fx-background-color: LightGray;");
            }

            button.setOnAction(e -> picketIndex.set(picketNumber));

            MenuItem delete = new MenuItem("Удалить"); // TODO использовать UI Properties
            delete.setOnAction(e -> historyManager.performThenSnapshot(() -> sectionManager.remove(picketNumber)));

            MenuItem moveLeft = new MenuItem("Переместить влево");
            moveLeft.setOnAction(e -> historyManager.performThenSnapshot(() -> sectionManager.swap(picketNumber, picketNumber - 1)));

            MenuItem moveRight = new MenuItem("Переместить вправо");
            moveRight.setOnAction(e -> historyManager.performThenSnapshot(() -> sectionManager.swap(picketNumber, picketNumber + 1)));

            if (picketObservableList.size() == 1) {
                delete.setDisable(true);
                moveLeft.setDisable(true);
                moveRight.setDisable(true);
            }
            if (picketNumber == 0) {
                moveLeft.setDisable(true);
            }
            if (picketNumber == picketObservableList.size() - 1) {
                moveRight.setDisable(true);
            }

            ContextMenu contextMenu = new ContextMenu(delete, moveLeft, moveRight);
            button.setOnContextMenuRequested(e -> contextMenu.show(getStage(), e.getScreenX(), e.getScreenY()));
            buttons.add(button);
        }
        container.getChildren().setAll(buttons);
    }
}

package ru.nucodelabs.gem.view.main;

import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.app.model.SectionManager;
import ru.nucodelabs.gem.app.snapshot.HistoryManager;
import ru.nucodelabs.gem.view.AbstractController;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PicketsBarController extends AbstractController {

    private final ObservableObjectValue<Section> section;
    private final IntegerProperty picketIndex;

    @FXML
    private HBox container;

    @Inject
    private SectionManager sectionManager;
    @Inject
    private HistoryManager<Section> historyManager;

    @Inject
    public PicketsBarController(
            ObservableObjectValue<Section> section,
            IntegerProperty picketIndex) {

        this.section = section;
        this.picketIndex = picketIndex;
        section.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                update();
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
        List<Picket> pickets = section.get().getPickets();
        for (int i = 0; i < pickets.size(); i++) {
            final int picketNumber = i;
            Button button = new Button(pickets.get(picketNumber).getName());

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

            if (pickets.size() == 1) {
                delete.setDisable(true);
                moveLeft.setDisable(true);
                moveRight.setDisable(true);
            }
            if (picketNumber == 0) {
                moveLeft.setDisable(true);
            }
            if (picketNumber == pickets.size() - 1) {
                moveRight.setDisable(true);
            }

            ContextMenu contextMenu = new ContextMenu(delete, moveLeft, moveRight);
            button.setOnContextMenuRequested(e -> contextMenu.show(getStage(), e.getScreenX(), e.getScreenY()));
            buttons.add(button);
        }
        container.getChildren().setAll(buttons);
    }
}

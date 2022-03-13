package ru.nucodelabs.gem.view.main;

import com.google.common.eventbus.EventBus;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ru.nucodelabs.gem.core.events.ModificationType;
import ru.nucodelabs.gem.core.events.UpdateViewEvent;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PicketsBarController extends Controller {

    private Section section;
    private int currentPicket;
    private final EventBus eventBus;

    @FXML
    public HBox container;

    public PicketsBarController(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    protected Stage getStage() {
        return (Stage) container.getScene().getWindow();
    }

    public void update() {
        List<Button> buttons = new ArrayList<>();

        for (int i = 0; i < section.getPicketsCount(); i++) {
            final int picketNumber = i;
            Button button = new Button(section.getName(i));

            button.setOnAction(e -> {
                currentPicket = picketNumber;
                eventBus.post(new UpdateViewEvent(ModificationType.PICKETS_BAR_CHANGE));
            });

            MenuItem delete = new MenuItem("Удалить");
            delete.setOnAction(e -> {
                section.removePicket(picketNumber);
                eventBus.post(new UpdateViewEvent(ModificationType.PICKETS_BAR_CHANGE));
            });

            MenuItem moveLeft = new MenuItem("Переместить влево");
            moveLeft.setOnAction(e -> {
                section.swapPickets(picketNumber, picketNumber - 1);
                eventBus.post(new UpdateViewEvent(ModificationType.PICKETS_BAR_CHANGE));
            });

            MenuItem moveRight = new MenuItem("Переместить вправо");
            moveRight.setOnAction(e -> {
                section.swapPickets(picketNumber, picketNumber + 1);
                eventBus.post(new UpdateViewEvent(ModificationType.PICKETS_BAR_CHANGE));
            });

            if (section.getPicketsCount() == 1) {
                delete.setDisable(true);
                moveLeft.setDisable(true);
                moveRight.setDisable(true);
            }
            if (picketNumber == 0) {
                moveLeft.setDisable(true);
            }
            if (picketNumber == section.getPicketsCount() - 1) {
                moveRight.setDisable(true);
            }

            ContextMenu contextMenu = new ContextMenu(delete, moveLeft, moveRight);
            button.setOnContextMenuRequested(e -> contextMenu.show(getStage(), e.getScreenX(), e.getScreenY()));
            buttons.add(button);
        }

        container.getChildren().setAll(buttons);
    }

    public int getCurrentPicket() {
        return currentPicket;
    }
}

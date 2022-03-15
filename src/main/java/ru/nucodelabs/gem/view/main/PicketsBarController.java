package ru.nucodelabs.gem.view.main;

import com.google.common.eventbus.EventBus;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ru.nucodelabs.gem.core.events.PicketSwitchEvent;
import ru.nucodelabs.gem.core.events.SectionChangeEvent;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.AbstractSectionController;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PicketsBarController extends AbstractSectionController {

    @FXML
    public HBox container;

    @Inject
    public PicketsBarController(EventBus eventBus, Section section) {
        super(eventBus, section);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    protected Stage getStage() {
        return (Stage) container.getScene().getWindow();
    }

    @Override
    protected void update() {
        List<Button> buttons = new ArrayList<>();

        for (int i = 0; i < section.getPicketsCount(); i++) {
            final int picketNumber = i;
            Button button = new Button(section.getName(i));

            if (i == currentPicket) {
                button.setStyle(
                        "-fx-background-color: LightGray;");
            }

            button.setOnAction(e -> {
                viewEvents.post(new PicketSwitchEvent(picketNumber));
            });

            MenuItem delete = new MenuItem("Удалить"); // TODO использовать UI Properties
            delete.setOnAction(e -> {
                section.removePicket(picketNumber);
                viewEvents.post(new SectionChangeEvent());
            });

            MenuItem moveLeft = new MenuItem("Переместить влево");
            moveLeft.setOnAction(e -> {
                section.swapPickets(picketNumber, picketNumber - 1);
                viewEvents.post(new SectionChangeEvent());
            });

            MenuItem moveRight = new MenuItem("Переместить вправо");
            moveRight.setOnAction(e -> {
                section.swapPickets(picketNumber, picketNumber + 1);
                viewEvents.post(new SectionChangeEvent());
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
}

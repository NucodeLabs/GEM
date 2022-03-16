package ru.nucodelabs.gem.view.main;

import io.reactivex.rxjava3.subjects.Subject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.Controller;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PicketsBarController implements Controller {

    @Inject
    private Section section;
    private Picket picket;

    private final Subject<Section> sectionSubject;
    private final Subject<Picket> picketSubject;

    @FXML
    public HBox container;

    @Inject
    public PicketsBarController(
            Subject<Section> sectionSubject,
            Subject<Picket> picketSubject) {
        this.sectionSubject = sectionSubject;
        this.picketSubject = picketSubject;
        sectionSubject
                .subscribe(section1 -> {
                    section = section1;
                    update();
                });
        picketSubject
                .subscribe(picket1 -> {
                    picket = picket1;
                    update();
                });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public Stage getStage() {
        return (Stage) container.getScene().getWindow();
    }

    protected void update() {
        List<Button> buttons = new ArrayList<>();

        for (int i = 0; i < section.getPicketsCount(); i++) {
            final int picketNumber = i;
            Button button = new Button(section.getName(i));

            if (i == section.getPickets().indexOf(picket)) {
                button.setStyle(
                        "-fx-background-color: LightGray;");
            }

            button.setOnAction(e -> picketSubject.onNext(section.getPicket(picketNumber)));

            MenuItem delete = new MenuItem("Удалить"); // TODO использовать UI Properties
            delete.setOnAction(e -> {
                section.removePicket(picketNumber);
                if (picketNumber >= section.getPicketsCount() - 1) {
                    picketSubject.onNext(section.getLastPicket());
                } else if (picketNumber == 0) {
                    picketSubject.onNext(section.getPicket(0));
                }
                sectionSubject.onNext(section);
            });

            MenuItem moveLeft = new MenuItem("Переместить влево");
            moveLeft.setOnAction(e -> {
                section.swapPickets(picketNumber, picketNumber - 1);
                sectionSubject.onNext(section);
            });

            MenuItem moveRight = new MenuItem("Переместить вправо");
            moveRight.setOnAction(e -> {
                section.swapPickets(picketNumber, picketNumber + 1);
                sectionSubject.onNext(section);
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

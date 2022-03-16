package ru.nucodelabs.gem.view.main;

import com.google.inject.name.Named;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.nucodelabs.gem.core.events.SectionChangeEvent;
import ru.nucodelabs.gem.core.events.ViewEvent;
import ru.nucodelabs.gem.view.Controller;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class NoFileScreenController extends Controller {

    @Inject
    @Named("ImportEXP")
    private Runnable importEXP;
    @Inject
    @Named("OpenSection")
    private Runnable openSection;

    @Inject
    public NoFileScreenController(Subject<ViewEvent> viewEventSubject) {
        viewEventSubject
                .filter(e -> e instanceof SectionChangeEvent)
                .cast(SectionChangeEvent.class)
                .subscribe(this::handleSectionChangeEvent);
    }

    private void handleSectionChangeEvent(SectionChangeEvent event) {
        this.hide();
    }

    @FXML
    public VBox root;

    @FXML
    private void importEXP() {
        importEXP.run();
    }

    @FXML
    private void openSection() {
        openSection.run();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    protected Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    private void hide() {
        root.setVisible(false);
    }
}

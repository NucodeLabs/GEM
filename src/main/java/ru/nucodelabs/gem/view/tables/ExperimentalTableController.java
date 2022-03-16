package ru.nucodelabs.gem.view.tables;

import io.reactivex.rxjava3.core.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.ExperimentalTableLine;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.convert.VESTablesConverters;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class ExperimentalTableController implements Controller {

    private Picket picket;

    @FXML
    private TableView<ExperimentalTableLine> table;

    @Inject
    public ExperimentalTableController(Observable<Picket> picketObservable) {
        picketObservable
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
        return (Stage) table.getScene().getWindow();
    }

    protected void update() {
        table.itemsProperty().setValue(
                VESTablesConverters.toExperimentalTableData(
                        picket.experimentalData()
                )
        );
    }
}

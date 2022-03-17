package ru.nucodelabs.gem.view.tables;

import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.ModelTableLine;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.convert.VESTablesConverters;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class ModelTableController extends Controller {

    private Picket picket;

    @FXML
    private TableView<ModelTableLine> table;

    /**
     * Отображает модельные данные в таблице для конкретного пикета.
     * Если меняются только модельные данные, обновляется.
     *
     * @param picketObservable    пикет
     * @param modelDataObservable модельные данные
     */
    @Inject
    public ModelTableController(
            Observable<Picket> picketObservable,
            Observable<ModelData> modelDataObservable) {
        picketObservable
                .subscribe(picket1 -> {
                    picket = picket1;
                    update();
                });
        modelDataObservable
                .subscribe(modelData -> {
                    picket = new Picket(picket.name(), picket.experimentalData(), modelData);
                    update();
                });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    protected Stage getStage() {
        return (Stage) table.getScene().getWindow();
    }

    protected void update() {
        ObservableList<ModelTableLine> modelTableLines = FXCollections.emptyObservableList();

        if (picket.modelData() != null) {
            modelTableLines = VESTablesConverters.toModelTableData(
                    picket.modelData()
            );
        }

        table.itemsProperty().setValue(modelTableLines);
    }
}

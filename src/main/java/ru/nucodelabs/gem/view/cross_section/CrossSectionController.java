package ru.nucodelabs.gem.view.cross_section;

import com.google.inject.name.Named;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.dao.Section;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.convert.CrossSectionConverters;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CrossSectionController extends Controller {

    private ResourceBundle uiProperties;
    private ObservableList<Picket> picketObservableList;

    @FXML
    public CategoryAxis sectionBarChartXAxis;
    @FXML
    public NumberAxis sectionBarChartYAxis;
    @FXML
    StackedBarChart<String, Double> sectionBarChart;

    /*@Inject
    @Named("CrossSection")*/
    private ObjectProperty<ObservableList<XYChart.Series<String, Double>>> dataProperty;

    @Inject
    public CrossSectionController(ObservableList<Picket> picketObservableList) {
        this.picketObservableList = picketObservableList;
        update();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        uiProperties = resources;
        sectionBarChart.dataProperty().bind(dataProperty);

    }

    public void update() {
        ArrayList<Boolean> blanks = new ArrayList<>();
        int maxLayers = 0;

        //Помечаются валидные и null модели пикетов
        for (Picket p : picketObservableList) {
            if (p.modelData() == null) {
                blanks.add(false);
            } else {
                blanks.add(true);
            }
        }

        //Находится наибольшее число слоев среди всех пикетов разреза
        for (int i = 0; i < picketObservableList.size(); i++) {
            int picketSize = picketObservableList.get(i).modelData().getSize();
            if (blanks.get(i) && maxLayers < picketSize) {
                maxLayers = picketSize;
            }
        }

        if (maxLayers == 0) {
            maxLayers = 1;
        }

        //Создаются Series из всех еще непроверенных нижних слоев пикетов.
        //Двигаясь снизу вверх получается несколько "слоев" слоев из которых можно будет потом сложить StackedBarChart.
        for (int i = 0; i < maxLayers; i++) {
            XYChart.Series<String, Double> tempSeries = CrossSectionConverters.getLayerOfPowers(picketObservableList, i);
            tempSeries.setName(((Integer) i).toString());

            dataProperty.get().add(new XYChart.Series<>());
            dataProperty.get().set(i, tempSeries);
        }
    }

    protected Stage getStage() {
        return (Stage) sectionBarChart.getScene().getWindow();
    }
}

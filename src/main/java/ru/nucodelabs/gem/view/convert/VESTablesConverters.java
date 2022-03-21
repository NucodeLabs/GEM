package ru.nucodelabs.gem.view.convert;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ExperimentalTableLine;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.ModelTableLine;

import java.util.ArrayList;
import java.util.List;

import static ru.nucodelabs.gem.view.charts.VESCurvesController.MOD_CURVE_SERIES_INDEX;

public class VESTablesConverters {

    private VESTablesConverters() {
    }

    public static ObservableList<ExperimentalTableLine> toExperimentalTableData(final ExperimentalData experimentalData) {
        if (experimentalData != null) {
            return FXCollections.observableList(experimentalData.getLines());
        } else {
            return FXCollections.emptyObservableList();
        }
    }

    public static ObservableList<ModelTableLine> toModelTableData(final ModelData modelData) {
        if (modelData != null) {
            return FXCollections.observableList(modelData.getLines());
        } else {
            return FXCollections.emptyObservableList();
        }
    }

    //Недоделано
    public static ObservableList<ModelTableLine> seriesToModelTableData(ObservableList<XYChart.Series<Double, Double>> series) {
        if (series != null) {
            List<ModelTableLine> modelDataList = new ArrayList<>();
            int index = 0;
            for (XYChart.Data<Double, Double> data : series.get(MOD_CURVE_SERIES_INDEX).getData()) {
                //ModelTableLine tempLine = new ModelTableLine(index++, )
            }

            ObservableList<ModelTableLine> list = FXCollections.observableList(modelDataList);
            return list;
        } else {
            return FXCollections.emptyObservableList();
        }
    }
}

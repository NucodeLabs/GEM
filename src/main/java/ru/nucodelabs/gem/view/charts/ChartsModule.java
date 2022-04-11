package ru.nucodelabs.gem.view.charts;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import ru.nucodelabs.algorithms.charts.VESChartsService;

import java.util.ArrayList;

import static ru.nucodelabs.gem.view.charts.VESCurvesController.MOD_CURVE_SERIES_CNT;

public class ChartsModule extends AbstractModule {
    @Provides
    private ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> emptyChartData() {
        return new SimpleObjectProperty<>(
                FXCollections.observableArrayList(new ArrayList<>()));
    }

    @Provides
    @Named("VESCurves")
    private ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> provideVESCurvesData() {
        ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> dataProperty =
                new SimpleObjectProperty<>(FXCollections.observableArrayList());
        for (int i = 0; i < MOD_CURVE_SERIES_CNT; i++) {
            dataProperty.get().add(new XYChart.Series<>());
        }
        return dataProperty;
    }

    @Provides
    @Singleton
    private VESChartsService pointsService() {
        return new VESChartsService();
    }
}

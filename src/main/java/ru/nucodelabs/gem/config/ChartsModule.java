package ru.nucodelabs.gem.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import ru.nucodelabs.geo.forward.ForwardSolver;
import ru.nucodelabs.geo.ves.calc.graph.MisfitsFunction;

import java.util.ArrayList;

import static ru.nucodelabs.gem.view.controller.charts.VesCurvesController.TOTAL_COUNT;

public class ChartsModule extends AbstractModule {
    @Provides
    private ObjectProperty<ObservableList<XYChart.Series<Number, Number>>> emptyChartData() {
        return new SimpleObjectProperty<>(
                FXCollections.observableArrayList(new ArrayList<>()));
    }

    @Provides
    @Named("VESCurves")
    private ObjectProperty<ObservableList<XYChart.Series<Number, Number>>> provideVESCurvesData() {
        ObjectProperty<ObservableList<XYChart.Series<Number, Number>>> dataProperty =
                new SimpleObjectProperty<>(FXCollections.observableArrayList());
        for (int i = 0; i < TOTAL_COUNT; i++) {
            dataProperty.get().add(new XYChart.Series<>());
        }
        return dataProperty;
    }

    @Provides
    MisfitsFunction misfitValuesFactory(ForwardSolver forwardSolver) {
        return MisfitsFunction.createDefault(forwardSolver);
    }
}

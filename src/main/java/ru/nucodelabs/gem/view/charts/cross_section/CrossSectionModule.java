package ru.nucodelabs.gem.view.charts.cross_section;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import ru.nucodelabs.files.color_palette.CLRData;
import ru.nucodelabs.files.color_palette.CLRFileParser;

import java.io.File;
import java.util.ArrayList;

public class CrossSectionModule extends AbstractModule {
    @Provides
    private ObjectProperty<ObservableList<XYChart.Series<Number, Number>>> provideEmptyCrossSectionData() {
        return new SimpleObjectProperty<>(
                FXCollections.observableArrayList(new ArrayList<>()));
    }
}

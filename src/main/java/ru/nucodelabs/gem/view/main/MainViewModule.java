package ru.nucodelabs.gem.view.main;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Named;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.dao.Section;
import ru.nucodelabs.gem.dao.SectionFactory;
import ru.nucodelabs.gem.dao.SectionImpl;

import java.util.ArrayList;

import static com.google.inject.Scopes.SINGLETON;
import static ru.nucodelabs.gem.view.charts.VESCurvesController.MOD_CURVE_SERIES_CNT;

/**
 * Зависимости в пределах одного главного окна
 * (при создании нового используются новые синглтоны, разделяемые контроллерами)
 */
public class MainViewModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(Section.class, SectionImpl.class)
                .build(SectionFactory.class));
        bind(Section.class).to(SectionImpl.class);
        bind(MainViewController.class).in(SINGLETON);
        bind(new TypeLiteral<ObjectProperty<Picket>>() {})
                .to(new TypeLiteral<SimpleObjectProperty<Picket>>() {})
                .in(SINGLETON);
        bind(new TypeLiteral<ObservableObjectValue<Picket>>() {})
                .to(new TypeLiteral<ObjectProperty<Picket>>() {});
    }

    @Provides
    @Named("ImportEXP")
    private Runnable provideImportEXP(MainViewController controller) {
        return controller::importEXP;
    }

    @Provides
    @Named("OpenJSON")
    private Runnable provideOpenSection(MainViewController controller) {
        return controller::openSection;
    }

    @Provides
    private ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> provideChartDataProperty() {
        return new SimpleObjectProperty<>(
                FXCollections.observableArrayList(new ArrayList<>()));
    }

    @Provides
    @Named("VESCurves")
    private ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> provideVESCurvesDataProperty() {
        ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> dataProperty =
                new SimpleObjectProperty<>(FXCollections.observableArrayList());
        for (int i = 0; i < MOD_CURVE_SERIES_CNT; i++) {
            dataProperty.get().add(new XYChart.Series<>());
        }
        return dataProperty;
    }

    @Provides
    @Singleton
    private ObservableList<Picket> providePickets() {
        return FXCollections.observableList(new ArrayList<>());
    }

    @Provides
    @Singleton
    private IntegerProperty provideIntProperty() {
        return new SimpleIntegerProperty(0);
    }
}

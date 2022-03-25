package ru.nucodelabs.gem.view;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.nucodelabs.data.ves.Picket;

import java.util.ArrayList;

/**
 * Используются для коммуникации между контроллерами
 */
public class SharedObservablesModule extends AbstractModule {
    /**
     * Observable список отображаемых пикетов - разрез
     */
    @Provides
    @Singleton
    private ObservableList<Picket> providePickets() {
        return FXCollections.observableList(new ArrayList<>());
    }

    /**
     * Индекс текущего отображаемого пикета для контроллеров, которые с ним взаимодействуют
     */
    @Provides
    @Singleton
    private IntegerProperty providePicketIndexProperty() {
        return new SimpleIntegerProperty(0);
    }

    /**
     * Текущий отображаемый пикет для контроллеров, которые отображают один пикет
     */
    @Provides
    @Singleton
    private ObjectProperty<Picket> providePicketProperty() {
        return new SimpleObjectProperty<>();
    }

    /**
     * Текущий отображаемый пикет для контроллеров которые его не модифицируют (урезанный интерфейс)
     */
    @Provides
    private ObservableObjectValue<Picket> providePicketObservable(ObjectProperty<Picket> picketObjectProperty) {
        return picketObjectProperty;
    }
}

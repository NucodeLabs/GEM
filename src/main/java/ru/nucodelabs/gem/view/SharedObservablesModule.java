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

    @Provides
    @Singleton
    private ObjectProperty<Picket> providePicketProperty() {
        return new SimpleObjectProperty<>();
    }

    @Provides
    private ObservableObjectValue<Picket> providePicketObservable(ObjectProperty<Picket> picketObjectProperty) {
        return picketObjectProperty;
    }
}

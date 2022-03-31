package ru.nucodelabs.gem.view.main;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.app.annotation.Subject;

import java.util.ArrayList;

/**
 * Используются для коммуникации между контроллерами
 */
public class ObservableDataModule extends AbstractModule {
    /**
     * Observable список отображаемых пикетов - разрез
     */
    @Provides
    @Singleton
    @Subject
    private ObservableList<Picket> providePicketsObservableList() {
        return FXCollections.observableList(new ArrayList<>());
    }

    /**
     * Индекс текущего отображаемого пикета для контроллеров, которые с ним взаимодействуют
     */
    @Provides
    @Singleton
    private IntegerProperty providePicketIndex() {
        return new SimpleIntegerProperty(0);
    }

    /**
     * Текущий отображаемый пикет, привязан к списку пикетов и индексу, потому его нельзя модифицировать
     * (вызвать set извне)
     */
    @Provides
    @Singleton
    private ObservableObjectValue<Picket> provideBoundCurrentPicket(
            IntegerProperty picketIndex,
            @Subject ObservableList<Picket> picketObservableList) {

        ObjectProperty<Picket> picket = new SimpleObjectProperty<>();
        picket.bind(new ObjectBinding<>() {
            {
                super.bind(picketIndex, picketObservableList);
            }

            @Override
            protected Picket computeValue() {
                if (picketObservableList.isEmpty()) {
                    return null;
                } else {
                    if (picketIndex.get() >= picketObservableList.size()) {
                        picketIndex.set(picketObservableList.size() - 1);
                    }
                    return picketObservableList.get(picketIndex.get());
                }
            }
        });


        return picket;
    }

    /**
     * Текущий отображаемый пикет, но который можно модифицировать. При это модификации попадут сразу в
     * picketObservableList и привязанный к нему пикет обновится.
     *
     * @param boundPicket          привязанный пикет
     * @param picketObservableList список пикетов
     * @param picketIndex          индекс пикета
     */
    @Provides
    @Singleton
    private ObjectProperty<Picket> provideWriteableCurrentPicket(
            ObservableObjectValue<Picket> boundPicket,
            @Subject ObservableList<Picket> picketObservableList,
            IntegerProperty picketIndex) {
        ObjectProperty<Picket> unboundPicket = new SimpleObjectProperty<>();

        boundPicket.addListener((observable, oldValue, newValue) -> unboundPicket.set(newValue));

        unboundPicket.addListener((observable, oldValue, newValue) -> {
            if (picketObservableList.stream().noneMatch(p -> p.equals(newValue))
                    && !picketObservableList.isEmpty()) {
                picketObservableList.set(picketIndex.get(), newValue);
            }
        });

        return unboundPicket;
    }
}

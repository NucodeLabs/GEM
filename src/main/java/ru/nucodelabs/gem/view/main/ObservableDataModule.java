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
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.app.model.AbstractSectionObserver;
import ru.nucodelabs.gem.app.model.SectionManager;

import java.util.ArrayList;

/**
 * Используются для коммуникации между контроллерами и удобного отслеживания изменений в данных.
 */
public class ObservableDataModule extends AbstractModule {
    /**
     * Observable список отображаемых пикетов - разрез
     */
    @Provides
    @Singleton
    private ObservableList<Picket> providePicketsObservableList(SectionManager sectionManager) {
        ObservableList<Picket> pickets = FXCollections.observableList(new ArrayList<>());
        sectionManager.subscribe(new AbstractSectionObserver() {
            @Override
            public void onNext(Section item) {
                if (!item.getPickets().equals(pickets)) {
                    pickets.setAll(item.getPickets());
                }
            }
        });
        return pickets;
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
            ObservableList<Picket> picketObservableList) {

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
}

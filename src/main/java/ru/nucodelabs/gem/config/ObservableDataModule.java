package ru.nucodelabs.gem.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import ru.nucodelabs.gem.fxmodel.ObservableSection;
import ru.nucodelabs.geo.ves.Picket;

import static javafx.beans.binding.Bindings.createObjectBinding;

/**
 * Используются для коммуникации между контроллерами и удобного отслеживания изменений в данных.
 */
public class ObservableDataModule extends AbstractModule {
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
            ObservableSection section) {
        ObjectProperty<Picket> picket = new SimpleObjectProperty<>();
        picket.bind(createObjectBinding(
                () -> {
                    if (section.getPickets().isEmpty()) {
                        return null;
                    } else {
                        if (picketIndex.get() >= section.getPickets().size()) {
                            picketIndex.set(section.getPickets().size() - 1);
                        }
                        return section.getPickets().get(picketIndex.get());
                    }
                },
                picketIndex, section.getPickets()
        ));
        return picket;
    }
}

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
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.app.model.AbstractSectionObserver;
import ru.nucodelabs.gem.app.model.SectionManager;

/**
 * Используются для коммуникации между контроллерами и удобного отслеживания изменений в данных.
 */
public class ObservableDataModule extends AbstractModule {
    @Provides
    @Singleton
    private ObservableObjectValue<Section> sectionObservableObjectValue(SectionManager sectionManager) {
        ObjectProperty<Section> sectionObjectProperty = new SimpleObjectProperty<>(sectionManager.getSnapshot().value());
        sectionManager.getSectionObservable().subscribe(new AbstractSectionObserver() {
            @Override
            public void onNext(Section item) {
                sectionObjectProperty.set(item);
            }
        });

        return sectionObjectProperty;
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
            ObservableObjectValue<Section> section) {

        ObjectProperty<Picket> picket = new SimpleObjectProperty<>();
        picket.bind(new ObjectBinding<>() {
            {
                super.bind(picketIndex, section);
            }

            @Override
            protected Picket computeValue() {
                if (section.get().getPickets().isEmpty()) {
                    return null;
                } else {
                    if (picketIndex.get() >= section.get().getPickets().size()) {
                        picketIndex.set(section.get().getPickets().size() - 1);
                    }
                    return section.get().getPickets().get(picketIndex.get());
                }
            }
        });


        return picket;
    }
}

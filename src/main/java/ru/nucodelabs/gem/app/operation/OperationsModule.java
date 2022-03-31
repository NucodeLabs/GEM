package ru.nucodelabs.gem.app.operation;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.app.annotation.Subject;

import java.util.List;

public class OperationsModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .build(ModelCurveDraggedOperation.Factory.class));
        install(new FactoryModuleBuilder()
                .build(PicketModificationOperation.Factory.class));
        install(new FactoryModuleBuilder()
                .build(RemovePicketOperation.Factory.class));
        install(new FactoryModuleBuilder()
                .build(SwapPicketsOperation.Factory.class));
        install(new FactoryModuleBuilder()
                .build(AddPicketOperation.Factory.class));
    }

    @Provides
    private int picketIndex(IntegerProperty picketIndex) {
        return picketIndex.get();
    }

    @Provides
    @Subject
    private List<Picket> currentState(@Subject ObservableList<Picket> picketObservableList) {
        return picketObservableList;
    }
}

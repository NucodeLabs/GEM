package ru.nucodelabs.gem.app.command;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import ru.nucodelabs.data.ves.Picket;

import java.util.List;

public class CommandsModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .build(ModelCurveDraggedCommand.Factory.class));
        install(new FactoryModuleBuilder()
                .build(PicketModificationCommand.Factory.class));
        install(new FactoryModuleBuilder()
                .build(RemovePicketCommand.Factory.class));
        install(new FactoryModuleBuilder()
                .build(SwapPicketsCommand.Factory.class));
        install(new FactoryModuleBuilder()
                .build(AddPicketCommand.Factory.class));
    }

    @Provides
    private int picketIndex(IntegerProperty picketIndex) {
        return picketIndex.get();
    }

    @Provides
    private List<Picket> currenState(ObservableList<Picket> picketObservableList) {
        return picketObservableList;
    }
}

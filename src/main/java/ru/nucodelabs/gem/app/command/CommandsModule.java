package ru.nucodelabs.gem.app.command;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class CommandsModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(ModelCurveDraggedCommand.class, ModelCurveDraggedCommand.class)
                .build(ModelCurveDraggedCommand.Factory.class));
        install(new FactoryModuleBuilder()
                .implement(PicketModificationCommand.class, PicketModificationCommand.class)
                .build(PicketModificationCommand.Factory.class));
    }
}

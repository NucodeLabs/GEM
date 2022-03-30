package ru.nucodelabs.gem.view.main;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import javafx.event.Event;
import javafx.event.EventHandler;
import ru.nucodelabs.gem.view.DialogsModule;
import ru.nucodelabs.gem.view.FileChoosersModule;
import ru.nucodelabs.gem.view.charts.ChartsModule;
import ru.nucodelabs.gem.view.charts.VESCurvesController;
import ru.nucodelabs.gem.view.tables.ExperimentalTableController;
import ru.nucodelabs.gem.view.tables.ModelTableController;

import static com.google.inject.Scopes.SINGLETON;

/**
 * Зависимости в пределах одного главного окна
 * (при создании нового используются новые синглтоны, разделяемые контроллерами)
 */
public class MainViewModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new FileChoosersModule());
        install(new DialogsModule());
        install(new ObservableDataModule());
        install(new ChartsModule());
        bind(MainViewController.class).in(SINGLETON);
        bind(VESCurvesController.class).in(SINGLETON);
        bind(ModelTableController.class).in(SINGLETON);
        bind(ExperimentalTableController.class).in(SINGLETON);
    }

    @Provides
    @Named("ImportEXP")
    private EventHandler<Event> provideImportEXP(MainViewController controller) {
        return event -> controller.importEXP();
    }

    @Provides
    @Named("OpenJSON")
    private EventHandler<Event> provideOpenSection(MainViewController controller) {
        return controller::openSection;
    }

    @Provides
    @Named("ImportMOD")
    private EventHandler<Event> provideImportMOD(MainViewController controller) {
        return event -> controller.importMOD();
    }
}

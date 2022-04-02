package ru.nucodelabs.gem.view.main;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.app.HistoryManager;
import ru.nucodelabs.gem.app.SectionManager;
import ru.nucodelabs.gem.app.io.StorageManager;
import ru.nucodelabs.gem.view.DialogsModule;
import ru.nucodelabs.gem.view.charts.ChartsModule;
import ru.nucodelabs.gem.view.charts.VESCurvesController;
import ru.nucodelabs.gem.view.tables.ExperimentalTableController;
import ru.nucodelabs.gem.view.tables.ModelTableController;

import java.util.ArrayList;

import static com.google.inject.Scopes.SINGLETON;

/**
 * Зависимости в пределах одного главного окна
 * (при создании нового используются новые синглтоны, разделяемые контроллерами)
 */
public class MainViewModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new DialogsModule());
        install(new ObservableDataModule());
        install(new ChartsModule());
        bind(MainViewController.class).in(SINGLETON);
        bind(VESCurvesController.class).in(SINGLETON);
        bind(ModelTableController.class).in(SINGLETON);
        bind(ExperimentalTableController.class).in(SINGLETON);
        bind(StorageManager.class).in(SINGLETON);
        bind(SectionManager.class).in(SINGLETON);
        bind(HistoryManager.class).in(SINGLETON);
    }

    @Provides
    @Named("Initial")
    private Section provideInitialSection() {
        return new Section(new ArrayList<>());
    }
}

package ru.nucodelabs.gem.view.main;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import javafx.stage.Stage;
import ru.nucodelabs.gem.app.AppService;
import ru.nucodelabs.gem.app.command.CommandsModule;
import ru.nucodelabs.gem.app.io.StorageManager;
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
        install(new CommandsModule());
        bind(MainViewController.class).in(SINGLETON);
        bind(VESCurvesController.class).in(SINGLETON);
        bind(ModelTableController.class).in(SINGLETON);
        bind(ExperimentalTableController.class).in(SINGLETON);
        bind(StorageManager.class).in(SINGLETON);
        bind(AppService.class).in(SINGLETON);
    }

    @Provides
    private Stage provideStage(MainViewController controller) {
        return controller.getStage();
    }
}

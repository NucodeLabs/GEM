package ru.nucodelabs.gem.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import ru.nucodelabs.gem.app.io.StorageManager;
import ru.nucodelabs.gem.fxmodel.ves.ObservableSection;
import ru.nucodelabs.gem.view.controller.FileImporter;
import ru.nucodelabs.gem.view.controller.FileOpener;
import ru.nucodelabs.gem.view.controller.main.MainViewController;
import ru.nucodelabs.geo.ves.Section;
import ru.nucodelabs.kfx.snapshot.HistoryManager;
import ru.nucodelabs.kfx.snapshot.Snapshot;

import static com.google.inject.Scopes.SINGLETON;

/**
 * Зависимости в пределах одного главного окна
 * (при создании нового используются новые синглтоны, разделяемые контроллерами)
 */
public class MainViewModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(MainViewController.class).in(SINGLETON);
        bind(StorageManager.class).in(SINGLETON);
        bind(ObservableSection.class).in(SINGLETON);

        install(new DialogsModule());
        install(new ObservableDataModule());
        install(new ChartsModule());
    }

    @Provides
    @Singleton
    private HistoryManager<Section> sectionHistoryManager(Snapshot.Originator<Section> sectionOriginator) {
        return new HistoryManager<>(sectionOriginator);
    }

    @Provides
    private Snapshot.Originator<Section> sectionOriginator(ObservableSection observableSection) {
        return observableSection;
    }

    @Provides
    private FileImporter fileImporter(MainViewController mainViewController) {
        return mainViewController;
    }

    @Provides
    private FileOpener fileOpener(MainViewController mainViewController) {
        return mainViewController;
    }
}

package ru.nucodelabs.gem.view.main;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.app.io.StorageManager;
import ru.nucodelabs.gem.app.model.SectionManager;
import ru.nucodelabs.gem.app.snapshot.HistoryManager;
import ru.nucodelabs.gem.app.snapshot.Snapshot;
import ru.nucodelabs.gem.view.DialogsModule;
import ru.nucodelabs.gem.view.charts.ChartsModule;
import ru.nucodelabs.gem.view.charts.VESCurvesController;
import ru.nucodelabs.gem.view.charts.cross_section.CrossSectionController;
import ru.nucodelabs.gem.view.charts.cross_section.CrossSectionModule;
import ru.nucodelabs.gem.view.color_palette.ColorPaletteController;
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
        install(new DialogsModule());
        install(new ObservableDataModule());
        install(new ChartsModule());
        install(new CrossSectionModule());

        bind(MainViewController.class).in(SINGLETON);
        bind(VESCurvesController.class).in(SINGLETON);
        bind(ModelTableController.class).in(SINGLETON);
        bind(ExperimentalTableController.class).in(SINGLETON);
        bind(CrossSectionController.class).in(SINGLETON);
        bind(ColorPaletteController.class).in(SINGLETON);

        bind(StorageManager.class).in(SINGLETON);
        bind(SectionManager.class).in(SINGLETON);
    }

    @Provides
    @Singleton
    private HistoryManager<Section> sectionHistoryManager(Snapshot.Originator<Section> sectionOriginator) {
        return new HistoryManager<>(sectionOriginator);
    }

    @Provides
    private Snapshot.Originator<Section> sectionOriginator(SectionManager sectionManager) {
        return sectionManager;
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

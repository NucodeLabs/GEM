package ru.nucodelabs.gem.view.main;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import ru.nucodelabs.gem.view.DialogsModule;
import ru.nucodelabs.gem.view.FileChoosersModule;
import ru.nucodelabs.gem.view.SharedObservablesModule;
import ru.nucodelabs.gem.view.charts.ChartsModule;

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
        install(new SharedObservablesModule());
        install(new ChartsModule());
        bind(MainViewController.class).in(SINGLETON);
    }

    @Provides
    @Named("ImportEXP")
    private Runnable provideImportEXP(MainViewController controller) {
        return controller::importEXP;
    }

    @Provides
    @Named("OpenJSON")
    private Runnable provideOpenSection(MainViewController controller) {
        return controller::openSection;
    }

    @Provides
    @Named("ImportMOD")
    private Runnable provideImportMOD(MainViewController controller) {
        return controller::importMOD;
    }
}

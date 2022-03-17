package ru.nucodelabs.gem.view.main;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.stage.FileChooser;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.dao.Section;
import ru.nucodelabs.gem.dao.SectionImpl;

import static com.google.inject.Scopes.SINGLETON;

/**
 * Зависимости в пределах одного главного окна
 * (при создании нового используются новые синглтоны, разделяемые контроллерами)
 */
public class MainViewModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Section.class).to(SectionImpl.class).in(SINGLETON);
        bind(MainViewController.class).in(SINGLETON);
        bind(new TypeLiteral<Subject<Section>>() {}).toProvider(PublishSubject::create).in(SINGLETON);
        bind(new TypeLiteral<Subject<Picket>>() {}).toProvider(PublishSubject::create).in(SINGLETON);
        bind(new TypeLiteral<Subject<ModelData>>() {}).toProvider(PublishSubject::create).in(SINGLETON);
        bind(new TypeLiteral<Observable<Section>>() {}).to(new TypeLiteral<Subject<Section>>() {});
        bind(new TypeLiteral<Observable<Picket>>() {}).to(new TypeLiteral<Subject<Picket>>() {});
        bind(new TypeLiteral<Observable<ModelData>>() {}).to(new TypeLiteral<Subject<ModelData>>() {});
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
    @Named("EXP")
    private FileChooser provideEXPFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("EXP - Полевые данные", "*.EXP", "*.exp")
        );
        return chooser;
    }

    @Provides
    @Named("JSON")
    private FileChooser provideJSONFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        return chooser;
    }

    @Provides
    @Named("MOD")
    private FileChooser provideMODFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MOD - Данные модели", "*.MOD", "*.mod")
        );
        return chooser;
    }
}

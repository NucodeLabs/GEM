package ru.nucodelabs.gem.view.main;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import ru.nucodelabs.gem.core.events.ViewEvent;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.model.SectionImpl;

import static com.google.inject.Scopes.SINGLETON;

public class MainViewModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Section.class).to(SectionImpl.class).in(SINGLETON);
    }

    @Singleton
    @Provides
    private Subject<ViewEvent> provideSubject() {
        return PublishSubject.create();
    }
}

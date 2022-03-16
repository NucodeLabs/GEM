package ru.nucodelabs.gem.view;

import io.reactivex.rxjava3.subjects.Subject;
import ru.nucodelabs.gem.core.events.PicketSwitchEvent;
import ru.nucodelabs.gem.core.events.SectionChangeEvent;
import ru.nucodelabs.gem.core.events.ViewEvent;
import ru.nucodelabs.gem.model.Section;

public abstract class AbstractSectionController extends Controller {
    protected final Section section;
    protected final Subject<ViewEvent> viewEvents;
    protected int currentPicket;

    public AbstractSectionController(Subject<ViewEvent> viewEvents, Section section) {
        this.viewEvents = viewEvents;
        this.section = section;
        // TODO split to 2 subjects
        this.viewEvents
                .filter(e -> e instanceof PicketSwitchEvent)
                .cast(PicketSwitchEvent.class)
                .subscribe(this::handlePicketSwitchEvent);
        this.viewEvents
                .filter(e -> e instanceof SectionChangeEvent)
                .cast(SectionChangeEvent.class)
                .subscribe(this::handleSectionChangeEvent);
    }

    protected void handlePicketSwitchEvent(PicketSwitchEvent event) {
        currentPicket = event.newPicketNumber();
        if (currentPicket >= section.getPicketsCount()) {
            viewEvents.onNext(new PicketSwitchEvent(section.getPicketsCount() - 1));
        } else if (currentPicket < 0) {
            viewEvents.onNext(new PicketSwitchEvent(0));
        } else {
            update();
        }
    }

    protected void handleSectionChangeEvent(SectionChangeEvent event) {
        if (currentPicket >= section.getPicketsCount()) {
            viewEvents.onNext(new PicketSwitchEvent(section.getPicketsCount() - 1));
        } else if (currentPicket < 0) {
            viewEvents.onNext(new PicketSwitchEvent(0));
        } else {
            update();
        }
    }

    protected abstract void update();
}

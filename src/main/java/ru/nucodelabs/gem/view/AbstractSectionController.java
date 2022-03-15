package ru.nucodelabs.gem.view;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import ru.nucodelabs.gem.core.events.PicketSwitchEvent;
import ru.nucodelabs.gem.core.events.SectionChangeEvent;
import ru.nucodelabs.gem.model.Section;

public abstract class AbstractSectionController extends Controller {
    protected final Section section;
    protected final EventBus viewEvents;
    protected int currentPicket;

    public AbstractSectionController(EventBus viewEvents, Section section) {
        this.viewEvents = viewEvents;
        this.section = section;
    }

    @Subscribe
    protected void handlePicketSwitchEvent(PicketSwitchEvent event) {
        currentPicket = event.newPicketNumber();
        if (currentPicket >= section.getPicketsCount()) {
            viewEvents.post(new PicketSwitchEvent(section.getPicketsCount() - 1));
        } else if (currentPicket < 0) {
            viewEvents.post(new PicketSwitchEvent(0));
        } else {
            update();
        }
    }

    @Subscribe
    protected void handleSectionChangeEvent(SectionChangeEvent event) {
        if (currentPicket >= section.getPicketsCount()) {
            viewEvents.post(new PicketSwitchEvent(section.getPicketsCount() - 1));
        } else if (currentPicket < 0) {
            viewEvents.post(new PicketSwitchEvent(0));
        } else {
            update();
        }
    }

    protected abstract void update();
}

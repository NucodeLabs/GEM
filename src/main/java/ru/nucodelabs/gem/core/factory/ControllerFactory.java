package ru.nucodelabs.gem.core.factory;

import com.google.common.eventbus.EventBus;
import javafx.util.Callback;
import ru.nucodelabs.gem.view.charts.MisfitStacksController;
import ru.nucodelabs.gem.view.charts.VESCurvesController;
import ru.nucodelabs.gem.view.main.MainViewController;
import ru.nucodelabs.gem.view.main.NoFileScreenController;
import ru.nucodelabs.gem.view.main.PicketsBarController;
import ru.nucodelabs.gem.view.tables.ExperimentalTableController;
import ru.nucodelabs.gem.view.tables.ModelTableController;

public class ControllerFactory implements Callback<Class<?>, Object> {

    private final EventBus appEvents;
    private final EventBusFactory eventBusFactory = new EventBusFactory();
    private final SectionFactory sectionFactory = new SectionFactory();

    public ControllerFactory(EventBus appEvents) {
        this.appEvents = appEvents;
    }

    @Override
    public Object call(Class<?> param) {
        if (param == MainViewController.class) {
            return new MainViewController(
                    appEvents,
                    eventBusFactory.create(),
                    sectionFactory.create());
        }
        if (param == NoFileScreenController.class) {
            return new NoFileScreenController();
        }
        if (param == MisfitStacksController.class) {
            return new MisfitStacksController(
                    eventBusFactory.getLastCreated(),
                    sectionFactory.getLastCreated());
        }
        if (param == VESCurvesController.class) {
            return new VESCurvesController(
                    eventBusFactory.getLastCreated(),
                    sectionFactory.getLastCreated());
        }
        if (param == ModelTableController.class) {
            return new ModelTableController(
                    eventBusFactory.getLastCreated(),
                    sectionFactory.getLastCreated());
        }
        if (param == ExperimentalTableController.class) {
            return new ExperimentalTableController(
                    eventBusFactory.getLastCreated(),
                    sectionFactory.getLastCreated());
        }
        if (param == PicketsBarController.class) {
            return new PicketsBarController(
                    eventBusFactory.getLastCreated(),
                    sectionFactory.getLastCreated());
        }
        throw new IllegalArgumentException();
    }
}

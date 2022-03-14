package ru.nucodelabs.gem.core;

import com.google.common.eventbus.EventBus;
import javafx.util.Callback;
import ru.nucodelabs.gem.model.SectionImpl;
import ru.nucodelabs.gem.view.charts.MisfitStacksController;
import ru.nucodelabs.gem.view.charts.VESCurvesController;
import ru.nucodelabs.gem.view.main.MainViewController;
import ru.nucodelabs.gem.view.main.NoFileScreenController;
import ru.nucodelabs.gem.view.main.PicketsBarController;
import ru.nucodelabs.gem.view.tables.ExperimentalTableController;
import ru.nucodelabs.gem.view.tables.ModelTableController;

public class ControllerFactory implements Callback<Class<?>, Object> {

    private final EventBus appEventBus;

    public ControllerFactory(EventBus appEventBus) {
        this.appEventBus = appEventBus;
    }

    @Override
    public Object call(Class<?> param) {
        if (param == MainViewController.class) {
            return new MainViewController(appEventBus, new SectionImpl());
        }
        if (param == NoFileScreenController.class) {
            return new NoFileScreenController();
        }
        if (param == MisfitStacksController.class) {
            return new MisfitStacksController();
        }
        if (param == VESCurvesController.class) {
            return new VESCurvesController();
        }
        if (param == ModelTableController.class) {
            return new ModelTableController();
        }
        if (param == ExperimentalTableController.class) {
            return new ExperimentalTableController();
        }
        if (param == PicketsBarController.class) {
            return new PicketsBarController();
        }
        throw new IllegalArgumentException();
    }
}

package ru.nucodelabs.gem.view;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.model.SectionImpl;
import ru.nucodelabs.gem.view.charts.MisfitStacksController;
import ru.nucodelabs.gem.view.charts.VESCurvesController;
import ru.nucodelabs.gem.view.main.MainViewController;
import ru.nucodelabs.gem.view.main.PicketsBarController;
import ru.nucodelabs.gem.view.tables.ExperimentalTableController;
import ru.nucodelabs.gem.view.tables.ModelTableController;

import static com.google.inject.Scopes.SINGLETON;

public class MainViewModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Section.class).to(SectionImpl.class).in(SINGLETON);
        bind(EventBus.class).in(SINGLETON);
        bind(MainViewController.class);
        bind(VESCurvesController.class);
        bind(MisfitStacksController.class);
        bind(ExperimentalTableController.class);
        bind(ModelTableController.class);
        bind(PicketsBarController.class);
    }
}

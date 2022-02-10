package ru.nucodelabs.gem.core;

import ru.nucodelabs.gem.view.main.MainViewModel;
import ru.nucodelabs.gem.view.welcome.WelcomeViewModel;

/**
 * Creates ViewModels with dependencies that they need
 */
public class ViewModelFactory {

    private final ModelFactory modelFactory;
    private ViewManager viewManager;

    public ViewModelFactory(ModelFactory modelFactory) {
        this.modelFactory = modelFactory;
    }

    public ViewModelFactory initViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
        return this;
    }

    private void checkViewManagerNotNull() {
        if (viewManager == null) {
            throw new NullPointerException("ViewManager is null.");
        }
    }

    public MainViewModel createMainViewModel() {
        checkViewManagerNotNull();
        return new MainViewModel(
                viewManager,
                modelFactory.getConfigModel(),
                modelFactory.createVESDataModel()
        );
    }

    public WelcomeViewModel createWelcomeViewModel() {
        checkViewManagerNotNull();
        return new WelcomeViewModel(viewManager);
    }
}

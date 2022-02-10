package ru.nucodelabs.gem.core;

import ru.nucodelabs.gem.view.main.MainViewModel;
import ru.nucodelabs.gem.view.welcome.WelcomeViewModel;

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

    public MainViewModel createMainViewModel() {
        return new MainViewModel(
                viewManager,
                modelFactory.getConfigModel(),
                modelFactory.createVESDataModel()
        );
    }

    public WelcomeViewModel createWelcomeViewModel() {
        return new WelcomeViewModel(viewManager);
    }
}

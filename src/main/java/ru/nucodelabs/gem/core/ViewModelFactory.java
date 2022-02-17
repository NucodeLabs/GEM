package ru.nucodelabs.gem.core;

import ru.nucodelabs.gem.view.main.MainViewModel;

import java.io.File;
import java.util.Objects;

/**
 * Creates ViewModels with dependencies that they need
 */
public class ViewModelFactory {

    private final ModelFactory modelFactory;
    private ViewManager viewManager;

    public ViewModelFactory(ModelFactory modelFactory) {
        this.modelFactory = modelFactory;
    }

    public void initViewManager(ViewManager viewManager) {
        Objects.requireNonNull(viewManager);
        this.viewManager = viewManager;
    }

    public MainViewModel createMainViewModel() {
        return new MainViewModel(
                viewManager,
                modelFactory.getConfigModel(),
                modelFactory.createVESDataModel()
        );
    }

    public MainViewModel createMainViewModel(File expFile) {
        MainViewModel mainViewModel = new MainViewModel(
                viewManager,
                modelFactory.getConfigModel(),
                modelFactory.createVESDataModel()
        );
        mainViewModel.addToCurrent(expFile);
        return mainViewModel;
    }
}

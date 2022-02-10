package ru.nucodelabs.mvvm;

import ru.nucodelabs.gem.core.ViewManager;

/**
 * Abstract view model class that should be extended by actual view models
 */
public abstract class ViewModel {

    protected final ViewManager viewManager;

    public ViewModel(ViewManager viewManager) {
        this.viewManager = viewManager;
    }
}

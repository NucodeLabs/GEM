package ru.nucodelabs.mvvm;

import ru.nucodelabs.gem.core.ViewManager;

public abstract class ViewModel {

    protected final ViewManager viewManager;

    public ViewModel(ViewManager viewManager) {
        this.viewManager = viewManager;
    }
}

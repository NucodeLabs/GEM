package ru.nucodelabs.mvvm;

import ru.nucodelabs.gem.core.ViewManager;

public abstract class ViewModel<M extends Model> {

    protected final M model;
    protected final ViewManager viewManager;

    public ViewModel(M model, ViewManager viewManager) {
        this.model = model;
        this.viewManager = viewManager;
    }
}

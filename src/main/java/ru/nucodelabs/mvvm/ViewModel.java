package ru.nucodelabs.mvvm;

import ru.nucodelabs.gem.core.ViewManager;

import java.util.HashMap;
import java.util.Map;

public abstract class ViewModel {

    protected final ViewManager viewManager;
    protected final Map<Class<? extends Model>, Model> models;

    public ViewModel(ViewManager viewManager, Model... models) {
        this.models = new HashMap<>();
        for (Model m : models) {
            this.models.put(m.getClass(), m);
        }
        this.viewManager = viewManager;
    }
}

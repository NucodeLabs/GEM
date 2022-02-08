package ru.nucodelabs.gem.core;

import ru.nucodelabs.mvvm.ViewModel;

import java.util.HashMap;
import java.util.Map;

public class ViewModelStorage {
    private final Map<Class<? extends ViewModel>, ViewModel> viewModelMap;

    public ViewModelStorage() {
        viewModelMap = new HashMap<>();
    }

    public void put(ViewModel viewModel) {
        viewModelMap.put(viewModel.getClass(), viewModel);
    }

    public ViewModel get(Class<? extends ViewModel> viewModelClass) {
        return viewModelMap.get(viewModelClass);
    }
}

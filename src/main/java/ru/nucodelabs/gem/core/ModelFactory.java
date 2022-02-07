package ru.nucodelabs.gem.core;

import ru.nucodelabs.mvvm.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * <h2>Model Factory</h2>
 * Gives references to models
 */
public class ModelFactory {

    private final Map<Class<? extends Model>, Model> modelMap;

    public ModelFactory() {
        modelMap = new HashMap<>();
    }

    public Model get(Class<? extends Model> modelClass) {
        if (modelMap.get(modelClass) != null) {
            return modelMap.get(modelClass);
        } else {
            Model model = null;
            try {
                model = modelClass.getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            modelMap.put(modelClass, model);
            return model;
        }
    }
}

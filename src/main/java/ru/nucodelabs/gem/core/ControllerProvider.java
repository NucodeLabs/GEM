package ru.nucodelabs.gem.core;

import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.main.MainViewController;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ControllerProvider {

    private final ModelProvider modelProvider;
    private final ViewManager viewManager;

    public ControllerProvider(ViewManager viewManager, ModelProvider modelProvider) {
        this.modelProvider = modelProvider;
        this.viewManager = viewManager;
    }

    public Controller create(Class<?> controllerClass) {
        if (controllerClass == MainViewController.class) {
            try {
                return createMainViewController();
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private MainViewController createMainViewController() throws InstantiationException, IllegalAccessException, InvocationTargetException {
        for (var constructor : MainViewController.class.getConstructors()) {
            if (Arrays.equals(constructor.getParameterTypes(), new Object[]{ViewManager.class, Section.class})) {
                return (MainViewController) constructor.newInstance(viewManager, modelProvider.getSection());
            }
        }
        return null;
    }
}

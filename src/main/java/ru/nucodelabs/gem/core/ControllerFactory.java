package ru.nucodelabs.gem.core;

import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.main.MainViewController;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;

public class ControllerFactory {

    private final ModelFactory modelFactory;
    private final ViewManager viewManager;

    public ControllerFactory(ViewManager viewManager, ModelFactory modelFactory) {
        this.modelFactory = modelFactory;
        this.viewManager = viewManager;
    }

    public Controller create(Class<?> controllerClass) {
        Objects.requireNonNull(modelFactory);
        Objects.requireNonNull(viewManager);
        if (controllerClass == MainViewController.class) {
            try {
                return createMainViewController(controllerClass);
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private MainViewController createMainViewController(Class<?> controllerClass) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        for (var constructor : controllerClass.getConstructors()) {
            if (constructor.getParameterCount() == 2) {
                if (Arrays.equals(constructor.getParameterTypes(), new Object[]{ViewManager.class, Section.class})) {
                    return (MainViewController) constructor.newInstance(viewManager, modelFactory.createSection());
                }
            }
        }
        return null;
    }
}

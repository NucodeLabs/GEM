package ru.nucodelabs.gem.app;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;

import javax.inject.Inject;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

public class PropertyConfigManager {

    private final Preferences preferences;

    @Inject
    public PropertyConfigManager(Preferences preferences) {
        this.preferences = preferences;
    }

    public void bind(DoubleProperty property, String propertyName, double def) {
        property.set(preferences.getDouble(propertyName, def));
        property.addListener((observable, oldValue, newValue) -> preferences.putDouble(propertyName, newValue.doubleValue()));
    }

    public void bind(ReadOnlyDoubleProperty property, String propertyName, double def, Consumer<Double> setter) {
        setter.accept(preferences.getDouble(propertyName, def));
        property.addListener((observable, oldValue, newValue) -> preferences.putDouble(propertyName, newValue.doubleValue()));
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void bind(BooleanProperty booleanProperty, String propertyName, boolean def) {
        booleanProperty.set(preferences.getBoolean(propertyName, def));
        booleanProperty.addListener((observable, oldValue, newValue) -> preferences.putBoolean(propertyName, newValue));
    }
}

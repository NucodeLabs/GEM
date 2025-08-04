package ru.nucodelabs.gem.app.pref

import jakarta.inject.Inject
import javafx.beans.property.*
import java.util.prefs.Preferences

/**
 * Helps to save properties values in Preferences object
 */
class FXPreferences @Inject constructor(val preferences: Preferences) {

    /**
     * Restores value from preferences and then saves future changes
     */
    fun setAndBind(property: DoubleProperty, propertyName: String, def: Double) {
        property.set(preferences.getDouble(propertyName, def))
        property.addListener { _, _, newValue: Number ->
            preferences.putDouble(
                propertyName,
                newValue.toDouble()
            )
        }
    }

    /**
     * Restores value from preferences and then saves future changes
     */
    fun setAndBind(booleanProperty: BooleanProperty, propertyName: String, def: Boolean) {
        booleanProperty.set(preferences.getBoolean(propertyName, def))
        booleanProperty.addListener { _, _, newValue: Boolean ->
            preferences.putBoolean(
                propertyName,
                newValue
            )
        }
    }

    /**
     * Restores value from preferences and then saves future changes
     */
    fun setAndBind(property: IntegerProperty, key: String, def: Int) {
        property.set(preferences.getInt(key, def))
        property.addListener { _, _, newValue -> preferences.putInt(key, newValue.toInt()) }
    }

    /**
     * Returns value that was saved in preferences and saves future changes
     */
    fun bind(property: ReadOnlyDoubleProperty, propertyName: String, def: Double): Double {
        property.addListener { _, _, newValue: Number ->
            preferences.putDouble(
                propertyName,
                newValue.toDouble()
            )
        }
        return preferences.getDouble(propertyName, def)
    }

    /**
     * Returns value that was saved in preferences and saves future changes
     */
    fun bind(property: ReadOnlyObjectProperty<Double>, key: String, def: Double): Double {
        property.addListener { _, _, newValue -> preferences.putDouble(key, newValue.toDouble()) }
        return preferences.getDouble(key, def)
    }

    /**
     * Returns value that was saved in preferences and saves future changes
     */
    fun bind(property: ReadOnlyObjectProperty<Int>, key: String, def: Int): Int {
        property.addListener { _, _, newValue -> preferences.putInt(key, newValue.toInt()) }
        return preferences.getInt(key, def)
    }
}
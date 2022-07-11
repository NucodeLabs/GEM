package ru.nucodelabs.gem.app.pref

import javafx.beans.property.*
import java.util.function.Consumer
import java.util.prefs.Preferences
import javax.inject.Inject

class FXPreferences @Inject constructor(val preferences: Preferences) {

    fun bind(property: DoubleProperty, propertyName: String?, def: Double) {
        property.set(preferences.getDouble(propertyName, def))
        property.addListener { _, _, newValue: Number ->
            preferences.putDouble(
                propertyName,
                newValue.toDouble()
            )
        }
    }

    fun bind(property: ReadOnlyDoubleProperty, propertyName: String?, def: Double, setter: Consumer<Double?>) {
        setter.accept(preferences.getDouble(propertyName, def))
        property.addListener { _, _, newValue: Number ->
            preferences.putDouble(
                propertyName,
                newValue.toDouble()
            )
        }
    }

    fun bind(booleanProperty: BooleanProperty, propertyName: String?, def: Boolean) {
        booleanProperty.set(preferences.getBoolean(propertyName, def))
        booleanProperty.addListener { _, _, newValue: Boolean? ->
            preferences.putBoolean(
                propertyName,
                newValue!!
            )
        }
    }

    fun bind(property: IntegerProperty, key: String, def: Int) {
        property.set(preferences.getInt(key, def))
        property.addListener { _, _, newValue -> preferences.putInt(key, newValue.toInt()) }
    }

    fun bind(property: ReadOnlyObjectProperty<Double>, key: String, def: Double): Double {
        property.addListener { _, _, newValue -> preferences.putDouble(key, newValue.toDouble()) }
        return preferences.getDouble(key, def)
    }

    fun bind(property: ReadOnlyObjectProperty<Int>, key: String, def: Int): Int {
        property.addListener { _, _, newValue -> preferences.putInt(key, newValue.toInt()) }
        return preferences.getInt(key, def)
    }
}
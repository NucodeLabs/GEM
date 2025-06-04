package ru.nucodelabs.gem.view.main

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableObjectValue
import ru.nucodelabs.gem.fxmodel.ObservableSection
import ru.nucodelabs.geo.ves.Picket
import javafx.beans.binding.Bindings

class ObservableDataModule : AbstractModule() {
    @Provides
    @Singleton
    fun providePicketIndex(): IntegerProperty = SimpleIntegerProperty(0)

    @Provides
    @Singleton
    fun provideBoundCurrentPicket(picketIndex: IntegerProperty, section: ObservableSection): ObservableObjectValue<Picket> {
        val picket: ObjectProperty<Picket> = SimpleObjectProperty()
        picket.bind(Bindings.createObjectBinding(
            {
                if (section.pickets.isEmpty()) null else {
                    if (picketIndex.get() >= section.pickets.size) {
                        picketIndex.set(section.pickets.size - 1)
                    }
                    section.pickets[picketIndex.get()]
                }
            }, picketIndex, section.pickets
        ))
        return picket
    }
}

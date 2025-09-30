package ru.nucodelabs.gem.fxmodel.ves.observable

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import ru.nucodelabs.geo.ves.SectionExperimentalDataSet

class ObservableSection : SectionExperimentalDataSet {
    val pickets: ObservableList<ObservablePicket> = FXCollections.observableArrayList()
    override fun pickets() = pickets
}
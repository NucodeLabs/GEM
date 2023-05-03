package ru.nucodelabs.gem.fxmodel.ves.app

import javafx.beans.property.IntegerProperty
import ru.nucodelabs.gem.app.snapshot.HistoryManager
import ru.nucodelabs.gem.fxmodel.ves.ObservableSection
import ru.nucodelabs.geo.ves.Section
import javax.inject.Inject

/**
 * Сюда по-хорошему надо вынести функционал из контроллеров
 */
class VesFxAppModel @Inject constructor(
    selectedIndexObservable: IntegerProperty,
    private val observableSection: ObservableSection,
    private val historyManager: HistoryManager<Section>,
    private val initialModelService: InitialModelService
) {
    private val selectedIndex: Int by selectedIndexObservable::value

    fun applySimpleInitialModel() {
        val picket = observableSection.pickets[selectedIndex]
        val model = initialModelService.simpleInitialModel(picket.effectiveExperimentalData)
        val newPicket = picket.copy(modelData = model)

        historyManager.snapshotAfter {
            observableSection.pickets[selectedIndex] = newPicket
        }
    }

    fun applyArbitraryInitialModel(parameters: ArbitraryInitialModelParameters) {
        val picket = observableSection.pickets[selectedIndex]
        val model = initialModelService.arbitraryInitialModel(
            signals = picket.effectiveExperimentalData,
            minTargetFunctionValue = parameters.minTargetFunctionValue,
            maxLayersCount = parameters.maxLayersCount
        )
        val newPicket = picket.copy(modelData = model)

        historyManager.snapshotAfter {
            observableSection.pickets[selectedIndex] = newPicket
        }
    }
}
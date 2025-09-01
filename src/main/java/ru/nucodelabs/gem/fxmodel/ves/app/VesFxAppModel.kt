package ru.nucodelabs.gem.fxmodel.ves.app

import jakarta.inject.Inject
import javafx.beans.property.IntegerProperty
import ru.nucodelabs.gem.fxmodel.ves.ObservableSection
import ru.nucodelabs.geo.ves.Picket
import ru.nucodelabs.geo.ves.Section
import ru.nucodelabs.geo.ves.calc.initialModel.MAX_LAYERS_COUNT
import ru.nucodelabs.geo.ves.calc.initialModel.MIN_TARGET_FUNCTION_VALUE
import ru.nucodelabs.kfx.snapshot.HistoryManager

/**
 * TODO: Сюда по-хорошему надо вынести функционал из контроллеров
 */
class VesFxAppModel @Inject constructor(
    selectedIndexObservable: IntegerProperty,
    private val observableSection: ObservableSection,
    private val historyManager: HistoryManager<Section>,
    private val initialModelService: InitialModelService,
    private val metricsService: MetricsService,
) {
    private val selectedIndex: Int by selectedIndexObservable::value

    private val picket: Picket
        get() = observableSection.pickets[selectedIndex]

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

    fun misfits(): List<Double> {
        return metricsService.misfitsValue(picket.effectiveExperimentalData, picket.modelData)
    }

    fun misfitsAvgMax(): AvgMax {
        return metricsService.misfitsAvgMax(picket.effectiveExperimentalData, picket.modelData)
    }

    fun error(): List<Double> {
        return metricsService.errorValue(picket.effectiveExperimentalData, picket.modelData)
    }

    fun errorAvgMax(): AvgMax {
        return metricsService.errorAvgMax(picket.effectiveExperimentalData, picket.modelData)
    }

    fun targetFunction(): Double {
        return metricsService.targetFunctionValue(picket.effectiveExperimentalData, picket.modelData)
    }

    companion object DefaultParameters {
        val DEFAULT_INITIAL_MODEL_PARAMETERS = ArbitraryInitialModelParameters(
            minTargetFunctionValue = MIN_TARGET_FUNCTION_VALUE,
            maxLayersCount = MAX_LAYERS_COUNT
        )
    }
}
package ru.nucodelabs.gem.view.charts

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.name.Named
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.chart.XYChart
import ru.nucodelabs.geo.ves.calc.graph.MisfitsFunction
import ru.nucodelabs.geo.ves.calc.forward.ForwardSolver

class ChartsModule : AbstractModule() {
    @Provides
    fun emptyChartData(): ObjectProperty<ObservableList<XYChart.Series<Number, Number>>> =
        SimpleObjectProperty(FXCollections.observableArrayList())

    @Provides
    @Named("VESCurves")
    fun provideVESCurvesData(): ObjectProperty<ObservableList<XYChart.Series<Number, Number>>> =
        SimpleObjectProperty(FXCollections.observableArrayList<XYChart.Series<Number, Number>>().apply {
            repeat(VesCurvesController.TOTAL_COUNT) { add(XYChart.Series()) }
        })

    @Provides
    fun misfitValuesFactory(forwardSolver: ForwardSolver): MisfitsFunction =
        MisfitsFunction.createDefault(forwardSolver)
}

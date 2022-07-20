package ru.nucodelabs.gem.view.charts

import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.util.StringConverter
import ru.nucodelabs.data.fx.ObservableSection
import javax.inject.Inject

class CurvesPseudoSectionController @Inject constructor(
    formatter: StringConverter<Number>,
    observableSection: ObservableSection,
) : AbstractPseudoSectionController(observableSection, formatter) {
    @FXML
    private lateinit var chart: LineChart<Number, Number>

    override fun update() {

    }
}
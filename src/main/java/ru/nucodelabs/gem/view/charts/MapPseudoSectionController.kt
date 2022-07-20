package ru.nucodelabs.gem.view.charts

import javafx.fxml.FXML
import javafx.scene.chart.XYChart
import javafx.stage.Stage
import javafx.util.StringConverter
import ru.nucodelabs.data.fx.ObservableSection
import ru.nucodelabs.data.ves.xOfPicket
import ru.nucodelabs.gem.extensions.fx.toObservableList
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.gem.view.control.chart.InterpolationMap
import java.net.URL
import java.util.*
import javax.inject.Inject

class MapPseudoSectionController @Inject constructor(
    private val colorMapper: ColorMapper,
    formatter: StringConverter<Number>,
    observableSection: ObservableSection,
) : AbstractPseudoSectionController(observableSection, formatter) {

    @FXML
    private lateinit var chart: InterpolationMap

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        chart.colorMapper = colorMapper
    }

    override val stage: Stage?
        get() = chart.scene.window as Stage?

    override fun update() {
        val section = observableSection.asSection()
        val data: MutableList<XYChart.Data<Number, Number>> = mutableListOf()

        for (picket in section.pickets) {
            for (expData in picket.effectiveExperimentalData) {
                data.add(
                    XYChart.Data(
                        section.xOfPicket(picket),
                        expData.ab2,
                        expData.resistanceApparent
                    )
                )
            }
        }

        chart.data.setAll(XYChart.Series(data.toObservableList()))
    }
}
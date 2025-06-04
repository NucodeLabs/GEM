package ru.nucodelabs.gem.view.color

import javafx.beans.property.*
import javafx.scene.paint.Color
import ru.nucodelabs.files.clr.ColorNode
import ru.nucodelabs.gem.util.std.exp10

class ColorPalette(
    private val valueColorList: List<ColorNode>,
    minValue: Double,
    maxValue: Double,
    blocksCount: Int
) : ColorMapper {
    private val minValueProperty = SimpleDoubleProperty()
    private val maxValueProperty = SimpleDoubleProperty()
    private val blocksCountProperty = SimpleIntegerProperty()
    private val logScaleProperty = SimpleBooleanProperty(false)
    private val segmentList: MutableList<ColorMapper.Segment> = mutableListOf()

    private var minValueInternal: Double = 0.0
    private var maxValueInternal: Double = 0.0

    init {
        minValueProperty.set(minValue)
        maxValueProperty.set(maxValue)
        blocksCountProperty.set(blocksCount)
        require(blocksCount >= 2) { "Число блоков меньше 2" }
        updateSegments()
        this.logScaleProperty.addListener { _, _, _ -> updateSegments() }
        this.blocksCountProperty.addListener { _, _, new ->
            require(new.toInt() >= 2) { "Число блоков меньше 2" }
            updateSegments()
        }
        this.minValueProperty.addListener { _, _, _ -> updateSegments() }
        this.maxValueProperty.addListener { _, _, _ -> updateSegments() }
    }

    private fun updateSegments() {
        segmentList.clear()
        checkLog()
        blocksInit()
    }

    private fun colorInterpolate(c1: Color, c2: Color, percentage: Double): Color {
        val r = c1.red + (c2.red - c1.red) * percentage
        val g = c1.green + (c2.green - c1.green) * percentage
        val b = c1.blue + (c2.blue - c1.blue) * percentage
        return Color(r, g, b, 1.0)
    }

    private fun vcInterpolate(vc1: ColorNode, vc2: ColorNode, percentage: Double): Color {
        val diff = (percentage - vc1.position) / (vc2.position - vc1.position)
        return colorInterpolate(vc1.color, vc2.color, diff)
    }

    private fun blockColor(from: Double, to: Double): Color {
        val vcsFrom = findNearestVCs(from)
        val vcsTo = findNearestVCs(to)
        val colorFrom = vcInterpolate(vcsFrom[0], vcsFrom[1], from)
        val colorTo = vcInterpolate(vcsTo[0], vcsTo[1], to)
        return colorInterpolate(colorFrom, colorTo, 0.5)
    }

    private fun findNearestVCs(percentage: Double): List<ColorNode> {
        for (i in 0 until valueColorList.size - 1) {
            val vcPercentage1 = valueColorList[i].position
            val vcPercentage2 = valueColorList[i + 1].position
            if (vcPercentage1 <= percentage && vcPercentage2 >= percentage) {
                return listOf(valueColorList[i], valueColorList[i + 1])
            }
        }
        error("Цвет не найден")
    }

    private fun blocksInit() {
        val firstColor = valueColorList.first().color
        val lastColor = valueColorList.last().color

        val blockSize = 1.0 / blocksCountProperty.get()
        var currentFrom = 0.0
        var currentTo = blockSize
        segmentList.add(ColorMapper.Segment(currentFrom, blockSize, firstColor))
        currentFrom += blockSize
        currentTo += blockSize
        for (i in 1 until blocksCountProperty.get() - 1) {
            segmentList.add(ColorMapper.Segment(currentFrom, currentTo, blockColor(currentFrom, currentTo)))
            currentFrom += blockSize
            currentTo += blockSize
        }
        segmentList.add(ColorMapper.Segment(currentFrom, 1.0, lastColor))
    }

    private fun blockFor(percentage: Double): ColorMapper.Segment {
        if (percentage == 1.0) return segmentList.last()
        return segmentList[(percentage / (1.0 / segmentList.size)).toInt()]
    }

    private fun percentageFor(resistance: Double): Double = when {
        resistance < minValueInternal -> 0.0
        resistance > maxValueInternal -> 1.0
        else -> (resistance - minValueInternal) / (maxValueInternal - minValueInternal)
    }

    private fun checkLog() {
        if (minValueProperty.get() < 0.1) minValueInternal = 0.1 else minValueInternal = minValueProperty.get()
        if (logScaleProperty.get()) {
            minValueInternal = kotlin.math.log10(minValueProperty.get())
            maxValueInternal = kotlin.math.log10(maxValueProperty.get())
        } else {
            minValueInternal = minValueProperty.get()
            maxValueInternal = maxValueProperty.get()
        }
    }

    override fun colorFor(value: Double): Color {
        val percentage = if (logScaleProperty.get()) percentageFor(kotlin.math.log10(value)) else percentageFor(value)
        return blockFor(percentage).color
    }

    override var minValue: Double
        get() = minValueProperty.get()
        set(value) { minValueProperty.set(value) }

    override var maxValue: Double
        get() = maxValueProperty.get()
        set(value) { maxValueProperty.set(value) }

    override var numberOfSegments: Int
        get() = blocksCountProperty.get()
        set(value) { blocksCountProperty.set(value) }

    override fun minValueProperty(): DoubleProperty = minValueProperty
    override fun maxValueProperty(): DoubleProperty = maxValueProperty
    override fun numberOfSegmentsProperty(): IntegerProperty = blocksCountProperty

    override val segments: List<ColorMapper.Segment>
        get() =
        if (!logScaleProperty.get()) segmentList.map {
            ColorMapper.Segment(
                it.from * (maxValueInternal - minValueInternal) + minValueInternal,
                it.to * (maxValueInternal - minValueInternal) + minValueInternal,
                it.color
            )
        } else segmentList.map {
            ColorMapper.Segment(
                logValue(valueFor(it.from, minValue, maxValue), minValue, maxValue),
                logValue(valueFor(it.to, minValue, maxValue), minValue, maxValue),
                it.color
            )
        }

    private fun valueFor(percentage: Double, minValue: Double, maxValue: Double) =
        percentage * (maxValue - minValue) + minValue

    private fun logValue(value: Double, minValue: Double, maxValue: Double): Double {
        val logRange = kotlin.math.log10(maxValue) - kotlin.math.log10(minValue)
        val range = maxValue - minValue
        val rDiv = range / logRange
        val logValue = (value - minValue) / rDiv + kotlin.math.log10(minValue)
        return exp10(logValue)
    }

    override fun logScaleProperty(): BooleanProperty = logScaleProperty
    override var isLogScale: Boolean
        get() = logScaleProperty.get()
        set(value) { logScaleProperty.set(value) }
}

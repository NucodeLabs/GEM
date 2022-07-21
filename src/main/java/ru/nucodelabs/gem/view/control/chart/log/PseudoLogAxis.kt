package ru.nucodelabs.gem.view.control.chart.log

import javafx.beans.NamedArg

class PseudoLogAxis @JvmOverloads constructor(
    @NamedArg("lowerBound") lowerBound: Double = 1.0,
    @NamedArg("upperBound") upperBound: Double = 100.0
): LogarithmicAxis(lowerBound, upperBound) {
    fun bindLogBoundsToDefaultBounds() {

    }
}
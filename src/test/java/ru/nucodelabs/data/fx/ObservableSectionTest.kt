package ru.nucodelabs.data.fx

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.nucodelabs.data.ves.Picket

internal class ObservableSectionTest {
    @Test
    fun asSection() {
        val pickets = listOf(Picket())
        val observableSection = ObservableSection(pickets)

        val sectionView = observableSection.asSection()
        observableSection.pickets += Picket()

        assertEquals(2, sectionView.pickets.size)

        val section = observableSection.toSection()
        observableSection.pickets += Picket()

        assertEquals(2, section.pickets.size)
    }
}
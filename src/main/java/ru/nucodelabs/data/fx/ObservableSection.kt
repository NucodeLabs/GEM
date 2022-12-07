package ru.nucodelabs.data.fx

import javafx.collections.ObservableList
import ru.nucodelabs.geo.ves.Picket
import ru.nucodelabs.geo.ves.Section
import ru.nucodelabs.gem.app.snapshot.Snapshot
import ru.nucodelabs.gem.app.snapshot.snapshotOf
import ru.nucodelabs.gem.extensions.fx.toObservableList

class ObservableSection(
    pickets: List<Picket> = listOf()
) : Snapshot.Originator<Section> {
    constructor(section: Section) : this(section.pickets)

    val pickets: ObservableList<Picket> = pickets.toObservableList()

    private val sectionView by lazy { Section(this.pickets) }

    fun asSection() = sectionView
    fun toSection() = Section(pickets.toList())

    override fun snapshot(): Snapshot<Section> = snapshotOf(toSection())
    override fun restoreFromSnapshot(snapshot: Snapshot<Section>) {
        pickets.setAll(snapshot.value.pickets)
    }
}

fun Section.toObservable() = ObservableSection(pickets)
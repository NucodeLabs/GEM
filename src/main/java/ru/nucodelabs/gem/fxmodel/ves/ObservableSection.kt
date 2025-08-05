package ru.nucodelabs.gem.fxmodel.ves

import javafx.collections.ObservableList
import ru.nucodelabs.geo.ves.Picket
import ru.nucodelabs.geo.ves.Section
import ru.nucodelabs.kfx.ext.toObservableList
import ru.nucodelabs.kfx.snapshot.Snapshot
import ru.nucodelabs.kfx.snapshot.snapshotOf

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
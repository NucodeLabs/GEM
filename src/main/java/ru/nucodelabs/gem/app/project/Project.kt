package ru.nucodelabs.gem.app.project

import ru.nucodelabs.kfx.snapshot.Snapshot
import ru.nucodelabs.kfx.snapshot.snapshotOf

data class Project<T>(
    var data: T
) : Snapshot.Originator<Project<T>> {
    override fun snapshot(): Snapshot<Project<T>> {
        return snapshotOf(this)
    }

    override fun restoreFromSnapshot(snapshot: Snapshot<Project<T>>) {
        if (this == snapshot.value) {
            return
        }

        data = snapshot.value.data
    }

}
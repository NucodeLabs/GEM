package ru.nucodelabs.gem.fxmodel.anisotropy.app

import ru.nucodelabs.gem.app.project.Project
import ru.nucodelabs.kfx.snapshot.HistoryManager
import ru.nucodelabs.kfx.snapshot.Snapshot
import ru.nucodelabs.kfx.snapshot.snapshotOf
import javax.inject.Inject

class ReloadService<T> @Inject constructor(
    private val historyManager: HistoryManager<Project<T>>,
    private val originator: Snapshot.Originator<Project<T>>
) {
    fun reloadProject(newProject: Project<T>) {
        originator.restoreFromSnapshot(snapshotOf(newProject))
        historyManager.clear()
        historyManager.snapshot()
    }
}
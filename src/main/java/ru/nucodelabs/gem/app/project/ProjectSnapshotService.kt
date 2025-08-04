package ru.nucodelabs.gem.app.project

import jakarta.inject.Inject
import ru.nucodelabs.kfx.snapshot.Snapshot
import ru.nucodelabs.kfx.snapshot.snapshotOf

abstract class ProjectSnapshotService<T> @Inject constructor(
    private val projectContext: ProjectContext<T>
) : Snapshot.Originator<Project<T>> {

    private val project by projectContext::project

    override fun snapshot(): Snapshot<Project<T>> {
        val snapshot = cloneProject(project)
        return snapshotOf(snapshot)
    }

    override fun restoreFromSnapshot(snapshot: Snapshot<Project<T>>) {
        project.data = snapshot.value.data
    }

    protected abstract fun cloneProject(project: Project<T>): Project<T>
}
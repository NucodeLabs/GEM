package ru.nucodelabs.gem.app.project

import ru.nucodelabs.gem.file.dto.project.ProjectDto
import ru.nucodelabs.kfx.snapshot.Snapshot
import ru.nucodelabs.kfx.snapshot.snapshotOf
import javax.inject.Inject

abstract class ProjectSnapshotService<T> @Inject constructor(
    private val projectContext: ProjectContext<T>
) : Snapshot.Originator<Project<T>> {

    private val project by projectContext::project

    override fun snapshot(): Snapshot<Project<T>> {
        val dto = mapToDto(project)
        val snapshot = mapFromDto(dto)
        return snapshotOf(snapshot)
    }

    override fun restoreFromSnapshot(snapshot: Snapshot<Project<T>>) {
        project.data = snapshot.value.data
    }

    protected abstract fun mapToDto(project: Project<T>): ProjectDto<*>
    protected abstract fun mapFromDto(projectDto: ProjectDto<*>): Project<T>
}
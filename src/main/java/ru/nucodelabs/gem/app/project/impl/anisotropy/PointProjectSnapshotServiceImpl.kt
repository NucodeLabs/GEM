package ru.nucodelabs.gem.app.project.impl.anisotropy

import jakarta.inject.Inject
import ru.nucodelabs.gem.app.project.Project
import ru.nucodelabs.gem.app.project.ProjectContext
import ru.nucodelabs.gem.app.project.ProjectSnapshotService
import ru.nucodelabs.gem.app.project.impl.anisotropy.cloner.PointProjectCloner
import ru.nucodelabs.geo.anisotropy.Point

class PointProjectSnapshotServiceImpl @Inject constructor(
    projectContext: ProjectContext<Point>,
    private val pointProjectCloner: PointProjectCloner
) : ProjectSnapshotService<Point>(projectContext) {

    override fun cloneProject(project: Project<Point>): Project<Point> {
        return pointProjectCloner.deepCopy(project)
    }

}
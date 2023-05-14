package ru.nucodelabs.gem.app.project.impl.anisotropy

import ru.nucodelabs.gem.app.project.Project
import ru.nucodelabs.gem.app.project.ProjectContext
import ru.nucodelabs.gem.app.project.ProjectSnapshotWrapper
import ru.nucodelabs.gem.app.project.impl.anisotropy.cloner.PointProjectCloner
import ru.nucodelabs.geo.anisotropy.Point
import javax.inject.Inject

class PointProjectSnapshotWrapperImpl @Inject constructor(
    projectContext: ProjectContext<Point>,
    private val pointProjectCloner: PointProjectCloner
) : ProjectSnapshotWrapper<Point>(projectContext) {

    override fun cloneProject(project: Project<Point>): Project<Point> {
        return pointProjectCloner.deepCopy(project)
    }

}
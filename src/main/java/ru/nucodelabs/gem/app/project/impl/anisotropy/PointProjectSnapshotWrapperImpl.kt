package ru.nucodelabs.gem.app.project.impl.anisotropy

import ru.nucodelabs.gem.app.project.Project
import ru.nucodelabs.gem.app.project.ProjectContext
import ru.nucodelabs.gem.app.project.ProjectSnapshotWrapper
import ru.nucodelabs.gem.file.dto.anisotropy.PointDto
import ru.nucodelabs.gem.file.dto.project.AnisotropyProjectDtoMapper
import ru.nucodelabs.gem.file.dto.project.ProjectDto
import ru.nucodelabs.geo.anisotropy.Point
import javax.inject.Inject

class PointProjectSnapshotWrapperImpl @Inject constructor(
    projectContext: ProjectContext<Point>,
    private val anisotropyProjectDtoMapper: AnisotropyProjectDtoMapper
) : ProjectSnapshotWrapper<Point>(projectContext) {
    override fun mapToDto(project: Project<Point>): ProjectDto<*> {
        return anisotropyProjectDtoMapper.toDto(project)
    }

    @Suppress("UNCHECKED_CAST")
    override fun mapFromDto(projectDto: ProjectDto<*>): Project<Point> {
        return anisotropyProjectDtoMapper.fromDto(projectDto as ProjectDto<PointDto>)
    }
}
package ru.nucodelabs.gem.app.project

import com.fasterxml.jackson.databind.ObjectMapper
import ru.nucodelabs.gem.file.dto.anisotropy.PointDto
import ru.nucodelabs.gem.file.dto.project.AnisotropyProjectDtoMapper
import ru.nucodelabs.gem.file.dto.project.ProjectDto
import ru.nucodelabs.geo.anisotropy.Point
import javax.inject.Inject

class PointProjectFileServiceImpl @Inject constructor(
    objectMapper: ObjectMapper,
    private val dtoMapper: AnisotropyProjectDtoMapper
) : AbstractProjectFileServiceImpl<Point>(objectMapper) {
    @Suppress("UNCHECKED_CAST")
    override fun mapFromDto(projectDto: ProjectDto<*>): Project<Point> {
        return dtoMapper.fromDto(projectDto as ProjectDto<PointDto>)
    }

    override fun mapToDto(project: Project<Point>): ProjectDto<*> {
        return dtoMapper.toDto(project)
    }
}
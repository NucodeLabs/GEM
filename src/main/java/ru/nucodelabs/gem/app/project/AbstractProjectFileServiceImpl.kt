package ru.nucodelabs.gem.app.project

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import ru.nucodelabs.gem.file.dto.project.ProjectDto
import java.io.File

abstract class AbstractProjectFileServiceImpl<T> @Inject constructor(
    private val objectMapper: ObjectMapper
) : ProjectFileService<T> {
    private var lastSavedProject: Project<T>? = null
    private var lastSavedProjectFile: File? = null

    override fun loadProject(file: File): Project<T> {
        val project = mapFromDto(objectMapper.readValue(file))
        lastSavedProject = project
        lastSavedProjectFile = file
        return project
    }

    override fun saveProject(file: File, project: Project<T>) {
        objectMapper.writeValue(file, mapToDto(project))
        lastSavedProject = project
        lastSavedProjectFile = file
    }

    override fun lastSavedProject(): Project<T>? {
        return lastSavedProject
    }

    override fun lastSavedProjectFile(): File? {
        return lastSavedProjectFile
    }

    override fun resetSave() {
        lastSavedProject = null
        lastSavedProjectFile = null
    }

    protected abstract fun mapFromDto(projectDto: ProjectDto<*>): Project<T>

    protected abstract fun mapToDto(project: Project<T>): ProjectDto<*>
}
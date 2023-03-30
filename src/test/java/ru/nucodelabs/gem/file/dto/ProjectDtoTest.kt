package ru.nucodelabs.gem.file.dto

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Guice
import org.junit.jupiter.api.Test
import ru.nucodelabs.gem.config.MappersModule
import ru.nucodelabs.gem.file.dto.anisotropy.AzimuthSignalsDto
import ru.nucodelabs.gem.file.dto.anisotropy.PointDto
import ru.nucodelabs.gem.file.dto.project.ProjectDto

class ProjectDtoTest {
    val injector = Guice.createInjector(MappersModule())
    val objectMapper = injector.getInstance(ObjectMapper::class.java)

    @Test
    fun json() {
        val projectDto = ProjectDto(
            PointDto(
                center = null,
                azimuthSignals = listOf(
                    AzimuthSignalsDto(
                        azimuth = 0.0,
                        listOf()
                    )
                ),
                model = null,
                z = 0.0,
                comment = ""
            )
        )
        val json = objectMapper.writeValueAsString(projectDto)
        println(json)
        val deserialized = objectMapper.readValue<ProjectDto<*>>(json)
        println(deserialized)
    }
}
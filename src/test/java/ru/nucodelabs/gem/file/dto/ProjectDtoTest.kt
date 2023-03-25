package ru.nucodelabs.gem.file.dto

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Guice
import io.mockk.every
import io.mockk.mockk
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
        val projectDto = mockk<ProjectDto<PointDto>>()
        every { projectDto.data } returns mockk(relaxed = true)
        every { projectDto.data.azimuthSignals } returns listOf(
            AzimuthSignalsDto(
                0.0,
                listOf(mockk(relaxed = true))
            )
        )
        every { projectDto.data.model } returns listOf()
        val json = objectMapper.writeValueAsString(projectDto)
        println(json)
        val deserialized = objectMapper.readValue<ProjectDto<*>>(json)
        println(deserialized)
    }
}
package ru.nucodelabs.gem.app.io.project

import com.google.inject.Guice
import com.google.inject.Key
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import ru.nucodelabs.gem.app.project.Project
import ru.nucodelabs.gem.app.project.ProjectFileService
import ru.nucodelabs.gem.config.AnisotropyProjectModule
import ru.nucodelabs.gem.config.MappersModule
import ru.nucodelabs.geo.anisotropy.AzimuthSignals
import ru.nucodelabs.geo.anisotropy.Point
import ru.nucodelabs.geo.anisotropy.Signals
import java.io.File

class PointProjectFileServiceImplTest {

    var project: Project<Point> = mockk()

    init {
        every { project.data } returns mockk(relaxed = true)
        every { project.data.azimuthSignals } returns mutableListOf(
            AzimuthSignals(
                0.0,
                Signals(mutableListOf(mockk(relaxed = true)))
            )
        )
        every { project.data.model } returns mutableListOf()
    }

    val file =
        File.createTempFile("sample", "test").apply {
            writeText(
                """
                {
                  "data" : {
                    "type" : "ru.nucodelabs.gem.file.dto.anisotropy.PointDto",
                    "azimuthSignals" : [ {
                      "azimuth" : 0.0,
                      "signals" : [ {
                        "ab2" : 0.0,
                        "mn2" : 0.0,
                        "amperage" : 0.0,
                        "voltage" : 0.0,
                        "resistanceApparent" : 0.0,
                        "errorResistanceApparent" : 0.0,
                        "isHidden" : false
                      } ]
                    } ],
                    "model" : [ ]
                  }
                }
            """.trimIndent()
            )
        }

    val injector = Guice.createInjector(
        MappersModule(),
        AnisotropyProjectModule()
    )

    val projectFileService: ProjectFileService<Point> = injector.getInstance(
        object : Key<ProjectFileService<Point>>() {}
    )

    @Test
    fun loadProject() {
        assertDoesNotThrow { projectFileService.loadProject(file) }
    }

    @Test
    fun saveProject() {
        assertDoesNotThrow { projectFileService.saveProject(file, project) }
    }

    @Test
    fun lastSavedProject() {
    }

    @Test
    fun lastSavedProjectFile() {
    }
}
package ru.nucodelabs.gem.fxmodel.anisotropy.app

import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import ru.nucodelabs.gem.app.project.Project
import ru.nucodelabs.gem.app.project.ProjectFileService
import ru.nucodelabs.gem.fxmodel.anisotropy.ObservablePoint
import ru.nucodelabs.gem.fxmodel.anisotropy.mapper.AnisotropyFxModelMapper
import ru.nucodelabs.gem.net.MapImage
import ru.nucodelabs.geo.anisotropy.Point
import ru.nucodelabs.geo.anisotropy.calc.map.MapSizer
import ru.nucodelabs.geo.anisotropy.calc.map.maxAb2WithAzimuth
import ru.nucodelabs.kfx.ext.getValue
import ru.nucodelabs.kfx.snapshot.HistoryManager
import java.io.File
import javax.inject.Inject
import javax.inject.Named

class AnisotropyFxAppModel @Inject constructor(
    private val historyManager: HistoryManager<Project<Point>>,
    private val fxModelMapper: AnisotropyFxModelMapper,
    @Named("initial") private val project: Project<Point>,
    private val projectFileService: ProjectFileService<Point>,
    private val mapImageProvider: AnisotropyMapImageProvider
) {
    private val point by project::data

    private val observablePointProperty: ReadOnlyObjectProperty<ObservablePoint> =
        SimpleObjectProperty(fxModelMapper.toObservable(project.data))

    fun observablePointProperty() = observablePointProperty
    val observablePoint: ObservablePoint by observablePointProperty

    private fun doWithPoint(block: (Point) -> Unit) {
        historyManager.snapshotAfter {
            block(point)
        }
        fxModelMapper.updateObservable(observablePoint, point)
    }

    fun newProject() {
        project.restoreFromSnapshot(Project(Point()).snapshot())
        projectFileService.resetSave()
    }

    fun loadProject(file: File) {
        val loadedProject = projectFileService.loadProject(file)
        project.restoreFromSnapshot(loadedProject.snapshot())
        fxModelMapper.updateObservable(observablePoint, point)
    }

    fun saveProject(file: File) {
        projectFileService.saveProject(file, project)
    }

    fun isProjectSaved(): Boolean {
        return projectFileService.lastSavedProject() == project && projectFileService.lastSavedProjectFile() != null
    }

/*    fun mapDistance(): Double? {
        return if (point.center != null) {
            val mapSizer = MapSizer(
                center = point.center!!,
                maxAb2WithAzimuth(point.azimuthSignals),
                scale = 1.0
            )
            mapImageProvider.getRealSize(
                mapSizer = mapSizer
            )
        } else {
            null
        }
    }*/

    fun mapImage(): MapImage? {
        return if (point.center != null) {
            val mapSizer = MapSizer(
                center = point.center!!,
                maxAb2WithAzimuth(point.azimuthSignals),
                scale = 1.0
            )
            mapImageProvider.satImage(
                mapSizer = mapSizer
            )
        } else {
            null
        }
    }
}
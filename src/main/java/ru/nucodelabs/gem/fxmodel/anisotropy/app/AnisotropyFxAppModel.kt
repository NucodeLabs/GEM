package ru.nucodelabs.gem.fxmodel.anisotropy.app

import jakarta.validation.Validator
import ru.nucodelabs.gem.app.project.Project
import ru.nucodelabs.gem.app.project.ProjectContext
import ru.nucodelabs.gem.app.project.ProjectFileService
import ru.nucodelabs.gem.fxmodel.anisotropy.ObservablePoint
import ru.nucodelabs.gem.fxmodel.anisotropy.mapper.AnisotropyFxModelMapper
import ru.nucodelabs.gem.fxmodel.map.MapImageData
import ru.nucodelabs.geo.anisotropy.Point
import ru.nucodelabs.geo.anisotropy.calc.map.Wgs
import ru.nucodelabs.kfx.snapshot.HistoryManager
import java.io.File
import javax.inject.Inject

class AnisotropyFxAppModel @Inject constructor(
    private val historyManager: HistoryManager<Project<Point>>,
    private val fxModelMapper: AnisotropyFxModelMapper,
    private val projectContext: ProjectContext<Point>,
    private val projectFileService: ProjectFileService<Point>,
    private val mapImageProvider: AnisotropyMapImageProvider,
    private val validator: Validator,
    private val reloadService: ReloadService<Point>
) {

    private val project by projectContext::project
    private val point by project::data

    val observablePoint: ObservablePoint = fxModelMapper.toObservable(point)

    private fun updateObservable() {
        fxModelMapper.updateObservable(observablePoint, point)
    }

    /**
     * Сохраняет модификацию в истории,
     * обновляет JavaFX-модель
     */
    private fun modelModification(block: (Point) -> Unit) {
        historyManager.snapshotAfter {
            block(point)
        }
        updateObservable()
    }

    fun newProject() {
        reloadService.reloadProject(newProject)
        projectFileService.resetSave()
    }

    fun loadProject(file: File) {
        val loadedProject = projectFileService.loadProject(file)
        reloadService.reloadProject(loadedProject)
        updateObservable()
    }

    fun saveProject(file: File) {
        projectFileService.saveProject(file, project)
    }

    fun isProjectSaved(): Boolean {
        return projectFileService.lastSavedProject() == project && projectFileService.lastSavedProjectFile() != null
    }

    fun mapImage(size: Int): MapImageData? {
        return if (point.center != null) {
            mapImageProvider.satImage(
                center = point.center!!,
                signals = point.azimuthSignals.toList(),
                size = size
            )
        } else {
            null
        }
    }

    fun editCenter(latitude: Double, longitude: Double) {
        val newCenter = Wgs(longitude, latitude).validated()
        modelModification {
            it.center = newCenter
        }
    }

    /**
     * Возвращает тот же объект, если значения валиды, IllegalStageException иначе
     */
    private fun <T> T.validated(): T {
        val violations = validator.validate(this)
        if (violations.isNotEmpty()) {
            throw IllegalStateException(violations.toString())
        }
        return this
    }

    fun undo() {
        historyManager.undo()
        updateObservable()
    }

    fun redo() {
        historyManager.redo()
        updateObservable()
    }

    companion object Defaults {
        val newProject
            get() = Project(Point())
    }
}
package ru.nucodelabs.gem.app

import com.google.inject.Inject
import com.google.inject.Key
import com.google.inject.name.Names
import com.sun.glass.ui.Application
import javafx.fxml.FXMLLoader
import javafx.stage.Stage
import javafx.stage.Window
import javafx.stage.WindowEvent
import javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST
import ru.nucodelabs.gem.config.AppModule
import ru.nucodelabs.gem.config.ArgNames
import ru.nucodelabs.gem.config.slf4j
import ru.nucodelabs.gem.view.AlertsFactory
import ru.nucodelabs.gem.view.controller.main.MainViewController
import ru.nucodelabs.kfx.core.GuiceApplication
import ru.nucodelabs.util.OS.macOS
import java.io.File

/**
 * Приложение, создает главное окошко
 */
class GemApplication : GuiceApplication(AppModule()) {

    private val macOSHandledFiles: MutableList<String> = mutableListOf()

    val log = slf4j(this)

    @Inject
    lateinit var alertsFactory: AlertsFactory

    init {
        macOS {
            Application.GetApplication().eventHandler = object : Application.EventHandler() {
                override fun handleOpenFilesAction(
                    app: Application,
                    time: Long,
                    files: Array<String>
                ) {
                    macOSHandledFiles += files
                }

                override fun handleQuitAction(app: Application, time: Long) {
                    val windowsLastIdx = Window.getWindows().lastIndex
                    for (i in windowsLastIdx downTo 0) {
                        with(Window.getWindows()[i]) {
                            fireEvent(WindowEvent(this, WINDOW_CLOSE_REQUEST))
                        }
                    }
                }
            }
        }
    }

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        Thread.setDefaultUncaughtExceptionHandler(
            UncaughtExceptionHandler(
                alertsFactory,
                parameters.raw.contains("--disable-uncaught-error-files")
            )
        )
        val params: List<String> = parameters.raw + macOSHandledFiles
        if (
            params.any {
                it.endsWith(".EXP", ignoreCase = true) || it.endsWith(".json", ignoreCase = true)
            }
        ) {
            processParams(params)
        } else {
            log.info("Starting MainView without parameters")
            guiceInjector.getInstance(Key.get(Stage::class.java, Names.named(ArgNames.View.MAIN_VIEW))).show()
        }
    }

    @Throws(Exception::class)
    override fun stop() {
        log.info("Exiting")
    }

    private fun processParams(params: List<String>): Boolean {
        log.info("Parameters are $params")
        val expFiles = mutableListOf<File>()
        for (param in params) {
            if (param.endsWith(".EXP", ignoreCase = true)) {
                expFiles += File(param)
            } else if (param.endsWith(".json", ignoreCase = true)) {
                loadMainViewWithJsonFile(File(param))
                return true
            }
        }
        if (expFiles.isNotEmpty()) {
            loadMainViewWithEXPFiles(expFiles)
            return true
        }
        return false
    }

    private fun loadMainViewWithJsonFile(jsonFile: File) =
        fxmlLoaderAfterShow().getController<MainViewController>().run {
            log.info("Open JSON Section, file: ${jsonFile.absolutePath}")
            openJsonSection(jsonFile)
        }

    private fun loadMainViewWithEXPFiles(expFiles: List<File>) =
        fxmlLoaderAfterShow().getController<MainViewController>().run {
            expFiles.forEach {
                log.info("Import EXP, file: ${it.absolutePath}")
                importEXP(it)
            }
        }

    private fun fxmlLoaderAfterShow(): FXMLLoader = mainViewFxmlLoader().also { it.load<Stage>().show() }

    private fun mainViewFxmlLoader() =
        guiceInjector.getInstance(Key.get(FXMLLoader::class.java, Names.named(ArgNames.View.MAIN_VIEW)))
}
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
import ru.nucodelabs.gem.config.Name
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
        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler(alertsFactory))

        val params: List<String> = parameters.raw + macOSHandledFiles
        log.info("Parameters are $params")
        if (!handleFileParameters(params)) {
            log.info("Starting MainView without parameters")
            guiceInjector.getInstance(
                Key.get(
                    Stage::class.java,
                    Names.named(Name.View.MAIN_VIEW)
                )
            ).show()
        }
    }

    @Throws(Exception::class)
    override fun stop() {
        log.info("Exiting")
    }

    /**
     * true if files handled, false if no files to open detected
     */
    private fun handleFileParameters(params: List<String>): Boolean {

        val expFiles = params.filter { it.endsWith(".EXP", ignoreCase = true) }
        val jsonFiles = params.filter { it.endsWith(".json", ignoreCase = true) }

        loadMainViewWithEXPFiles(expFiles.map { File(it) })
        jsonFiles.forEach { jsonFile -> loadMainViewWithJsonFile(File(jsonFile)) }

        return expFiles.isNotEmpty() or jsonFiles.isNotEmpty()
    }

    private fun loadMainViewWithJsonFile(jsonFile: File) =
        fxmlLoaderAfterShow().getController<MainViewController>().run {
            log.info("Starting - Open JSON Section, file: ${jsonFile.name}")
            openJsonSection(jsonFile)
        }

    private fun loadMainViewWithEXPFiles(expFiles: List<File>) =
        fxmlLoaderAfterShow().getController<MainViewController>().run {
            expFiles.filter { it.exists() }.forEach {
                log.info("Starting - Import EXP file: ${it.name}")
                importEXP(it)
            }
        }

    private fun fxmlLoaderAfterShow(): FXMLLoader = mainViewFxmlLoader().also { it.load<Stage>().show() }

    private fun mainViewFxmlLoader() =
        guiceInjector.getInstance(Key.get(FXMLLoader::class.java, Names.named(Name.View.MAIN_VIEW)))
}
package ru.nucodelabs.gem.app

import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Key
import com.google.inject.name.Names
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.stage.Stage
import javafx.stage.Window
import javafx.stage.WindowEvent
import javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST
import ru.nucodelabs.gem.utils.OS.macOS
import ru.nucodelabs.gem.view.main.MainViewController
import java.io.File
import java.util.logging.Logger
import com.sun.glass.ui.Application as LowLevelApplication

/**
 * Приложение, создает главное окошко
 */
class GemApplication : Application() {

    private val macOSHandledFiles: MutableList<String> = mutableListOf()
    private val injector = Guice.createInjector(AppModule())

    @Inject
    lateinit var logger: Logger

    init {
        macOS {
            LowLevelApplication.GetApplication().eventHandler = object : LowLevelApplication.EventHandler() {
                override fun handleOpenFilesAction(
                    app: com.sun.glass.ui.Application,
                    time: Long,
                    files: Array<String>
                ) {
                    macOSHandledFiles += files
                }

                override fun handleQuitAction(app: LowLevelApplication, time: Long) {
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
    override fun init() {
        injector.injectMembers(this)
        logger.info("Injected")
    }

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        val params: List<String> = parameters.raw + macOSHandledFiles
        if (params.isNotEmpty()) {
            processParams(params)
        } else {
            logger.info("Starting MainView without parameters")
            injector.getInstance(Key.get(Stage::class.java, Names.named("MainView"))).show()
        }
    }

    @Throws(Exception::class)
    override fun stop() {
        logger.info("Exiting")
    }

    private fun processParams(params: List<String>) {
        logger.info("Parameters are $params")
        val expFiles = mutableListOf<File>()
        for (param in params) {
            if (param.endsWith(".EXP", ignoreCase = true)) {
                expFiles += File(param)
            } else if (param.endsWith("json", ignoreCase = true)) {
                loadMainViewWithJsonFile(File(param))
            }
        }
        if (expFiles.isNotEmpty()) {
            loadMainViewWithEXPFiles(expFiles)
        }
    }

    private fun loadMainViewWithJsonFile(jsonFile: File) =
        fxmlLoaderAfterShow().getController<MainViewController>().run {
            logger.info("Open JSON Section, file: ${jsonFile.absolutePath}")
            openJsonSection(jsonFile)
        }

    private fun loadMainViewWithEXPFiles(expFiles: List<File>) =
        fxmlLoaderAfterShow().getController<MainViewController>().run {
            expFiles.forEach {
                logger.info("Import EXP, file: ${it.absolutePath}")
                addEXP(it)
            }
        }

    private fun fxmlLoaderAfterShow(): FXMLLoader = mainViewFxmlLoader().also { it.load<Stage>().show() }

    private fun mainViewFxmlLoader() = injector.getInstance(Key.get(FXMLLoader::class.java, Names.named("MainView")))
}
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
import ru.nucodelabs.gem.utils.macOs
import ru.nucodelabs.gem.view.main.MainViewController
import java.io.File
import java.io.IOException
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
        macOs {
            LowLevelApplication.GetApplication().eventHandler = object : LowLevelApplication.EventHandler() {
                override fun handleOpenFilesAction(
                    app: com.sun.glass.ui.Application,
                    time: Long,
                    files: Array<String>
                ) {
                    macOSHandledFiles.addAll(listOf(*files))
                }

                override fun handleQuitAction(app: LowLevelApplication, time: Long) {
                    val winCnt = Window.getWindows().size
                    for (i in (0 until winCnt).reversed()) {
                        val window = Window.getWindows()[i]
                        window.fireEvent(WindowEvent(window, WINDOW_CLOSE_REQUEST))
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
        val params: MutableList<String> = parameters.raw.toMutableList()
        params.addAll(macOSHandledFiles)
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
                logger.info("Import EXP, file: $param")
                expFiles += File(param)
            } else if (param.endsWith("json", ignoreCase = true)) {
                loadMainViewWithJSONFile(File(param))
            }
        }
        if (expFiles.isNotEmpty()) {
            loadMainViewWithEXPFiles(expFiles)
        }
    }

    private fun loadMainViewWithJSONFile(jsonFile: File) {
        val fxmlLoader = loadFxmlAndShow()
        val controller = fxmlLoader.getController<MainViewController>()
        logger.info("Open JSON Section, file: " + jsonFile.absolutePath)
        controller.openJsonSection(jsonFile)
    }

    private fun loadMainViewWithEXPFiles(expFiles: List<File>) {
        val fxmlLoader = loadFxmlAndShow()
        val controller = fxmlLoader.getController<MainViewController>()
        expFiles.forEach { controller.addEXP(it) }
    }

    private fun loadFxmlAndShow(): FXMLLoader {
        val fxmlLoader = injector.getInstance(Key.get(FXMLLoader::class.java, Names.named("MainView")))
        try {
            fxmlLoader.load<Stage>().show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return fxmlLoader
    }
}
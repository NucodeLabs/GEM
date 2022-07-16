package ru.nucodelabs.gem.view.main

import com.google.inject.name.Named
import jakarta.validation.Validator
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.beans.value.ObservableObjectValue
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.event.Event
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.stage.WindowEvent
import ru.nucodelabs.algorithms.inverse_solver.InverseSolver
import ru.nucodelabs.data.fx.ObservableSection
import ru.nucodelabs.data.ves.Picket
import ru.nucodelabs.data.ves.Section
import ru.nucodelabs.data.ves.xOfPicket
import ru.nucodelabs.gem.app.io.StorageManager
import ru.nucodelabs.gem.app.pref.*
import ru.nucodelabs.gem.app.snapshot.HistoryManager
import ru.nucodelabs.gem.app.snapshot.snapshotOf
import ru.nucodelabs.gem.extensions.fx.getValue
import ru.nucodelabs.gem.extensions.fx.isValidBy
import ru.nucodelabs.gem.extensions.fx.setValue
import ru.nucodelabs.gem.util.FXUtils
import ru.nucodelabs.gem.util.OS.isMacOS
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.AlertsFactory
import ru.nucodelabs.gem.view.charts.MisfitStacksController
import ru.nucodelabs.gem.view.charts.VesCurvesController
import java.io.File
import java.net.URL
import java.text.DecimalFormat
import java.text.ParseException
import java.util.*
import java.util.prefs.PreferenceChangeEvent
import java.util.prefs.Preferences
import javax.inject.Inject
import javax.inject.Provider

class MainViewController @Inject constructor(
    @Named("MainView") private val mainViewProvider: Provider<Stage>,
    @Named("EXP") private val expFileChooser: FileChooser,
    @Named("MOD") private val modFileChooser: FileChooser,
    @Named("JSON") private val jsonFileChooser: FileChooser,
    @Named("Save") private val saveDialogProvider: Provider<Dialog<ButtonType>>,
    private val picketObservable: ObservableObjectValue<Picket>,
    private val picketIndexProperty: IntegerProperty,
    private val observableSection: ObservableSection,
    private val historyManager: HistoryManager<Section>,
    private val alertsFactory: AlertsFactory,
    private val storageManager: StorageManager,
    private val validator: Validator,
    private val preferences: Preferences,
    private val decimalFormat: DecimalFormat,
    private val fxPreferences: FXPreferences,
    private val inverseSolver: InverseSolver
) : AbstractController(), FileImporter, FileOpener {

    private val windowTitle: StringProperty = SimpleStringProperty("GEM")
    private val dirtyAsterisk: StringProperty = SimpleStringProperty("")

    private val noFileOpenedProperty: BooleanProperty = SimpleBooleanProperty(true)
    fun noFileOpenedProperty(): BooleanProperty = noFileOpenedProperty
    val noFileOpened: Boolean by noFileOpenedProperty

    private val vesNumberProperty: StringProperty = SimpleStringProperty()
    fun vesNumberProperty(): StringProperty = vesNumberProperty
    val vesNumber: String? by vesNumberProperty

    private var picketIndex by picketIndexProperty

    private val picket
        get() = picketObservable.get()!!

    @FXML
    private lateinit var inverseBtn: Button

    @FXML
    private lateinit var picketName: TextField

    @FXML
    private lateinit var xCoordLbl: Label

    @FXML
    private lateinit var picketZ: TextField

    @FXML
    private lateinit var picketOffsetX: TextField

    @FXML
    private lateinit var menuViewVESCurvesLegend: CheckMenuItem

    @FXML
    private lateinit var root: Stage

    @FXML
    private lateinit var menuBar: MenuBar

    @FXML
    private lateinit var menuView: Menu

    @FXML
    private lateinit var noFileScreenController: NoFileScreenController

    @FXML
    private lateinit var vesCurvesController: VesCurvesController

    @FXML
    private lateinit var misfitStacksController: MisfitStacksController

    override val stage: Stage
        get() = root

    override fun initialize(location: URL, resources: ResourceBundle) {
        stage.onCloseRequest = EventHandler { event: WindowEvent -> askToSave(event) }
        stage.scene.accelerators[KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN)] = Runnable { redo() }
        if (isMacOS) {
            val useSystemMenu = CheckMenuItem(resources.getString("useSystemMenu"))
            menuView.items.add(0, useSystemMenu)
            useSystemMenu.selectedProperty().bindBidirectional(menuBar.useSystemMenuBarProperty())
            val prefKey = "USE_SYSTEM_MENU"
            val defVal = true
            useSystemMenu.isSelected = preferences.getBoolean(prefKey, defVal)
            useSystemMenu.selectedProperty()
                .addListener { _: ObservableValue<out Boolean?>?, _: Boolean?, newValue: Boolean? ->
                    preferences.putBoolean(
                        prefKey,
                        newValue!!
                    )
                }
            preferences.addPreferenceChangeListener { evt: PreferenceChangeEvent ->
                Platform.runLater {
                    if (evt.key == prefKey) {
                        useSystemMenu.isSelected = java.lang.Boolean.parseBoolean(evt.newValue)
                    }
                }
            }
        }
        bind()
        initConfig()
        setupTextFields()
        syncMisfitAndVesXAxes()
        setupInverseBtn()
    }

    private fun setupTextFields() {
        picketOffsetX.isValidBy { validateDoubleInput(it) }
        picketZ.isValidBy { validateDoubleInput(it) }
    }

    private fun setupInverseBtn() {
        inverseBtn.disableProperty().bind(
            Bindings.createBooleanBinding(
                {
                    if (picketObservable.get() != null) {
                        picket.modelData.isEmpty() || picket.sortedExperimentalData.isEmpty()
                    } else {
                        false
                    }
                },
                picketObservable
            )
        )
    }

    private fun syncMisfitAndVesXAxes() {
        misfitStacksController.lineChartXAxis.lowerBoundProperty().bind(
            vesCurvesController.xAxis.lowerBoundProperty()
        )
        misfitStacksController.lineChartXAxis.upperBoundProperty().bind(
            vesCurvesController.xAxis.upperBoundProperty()
        )
    }

    private fun validateDoubleInput(s: String): Boolean {
        try {
            decimalFormat.parse(s)
        } catch (e: ParseException) {
            return false
        }
        return true
    }

    private fun initConfig() {
        stage.width = fxPreferences.bind(stage.widthProperty(), MAIN_WINDOW_W.key, MAIN_WINDOW_W.def)
        stage.height = fxPreferences.bind(stage.heightProperty(), MAIN_WINDOW_H.key, MAIN_WINDOW_H.def)
        stage.x = fxPreferences.bind(stage.xProperty(), MAIN_WINDOW_X.key, MAIN_WINDOW_X.def)
        stage.y = fxPreferences.bind(stage.yProperty(), MAIN_WINDOW_Y.key, MAIN_WINDOW_Y.def)
        fxPreferences.bind(
            menuViewVESCurvesLegend.selectedProperty(),
            VES_CURVES_LEGEND_VISIBLE.key,
            VES_CURVES_LEGEND_VISIBLE.def
        )
    }

    private fun bind() {
        noFileScreenController.visibleProperty().bind(noFileOpenedProperty)
        vesCurvesController.legendVisibleProperty().bind(menuViewVESCurvesLegend.selectedProperty())
        vesNumberProperty.bind(
            Bindings.createStringBinding(
                { (picketIndex + 1).toString() + "/" + observableSection.pickets.size },
                picketIndexProperty, observableSection.pickets
            )
        )
        picketObservable.addListener { _: ObservableValue<out Picket?>?, _: Picket?, newValue: Picket? ->
            if (newValue != null) {
                picketName.text = newValue.name
            } else {
                picketName.text = "-"
            }
        }
        noFileOpenedProperty.bind(
            Bindings.createBooleanBinding(
                { observableSection.pickets.isEmpty() },
                observableSection.pickets
            )
        )
        observableSection.pickets.addListener(ListChangeListener {
            if (it.next()) {
                if (storageManager.savedSnapshot != observableSection.snapshot()) {
                    dirtyAsterisk.set("*")
                } else {
                    dirtyAsterisk.set("")
                }
            }
        })
        stage.titleProperty().bind(Bindings.concat(dirtyAsterisk, windowTitle))
        picketObservable.addListener { _: ObservableValue<out Picket?>?, _: Picket?, newValue: Picket? ->
            if (newValue != null) {
                picketOffsetX.text = decimalFormat.format(newValue.offsetX)
                picketZ.text = decimalFormat.format(newValue.z)
                xCoordLbl.text = decimalFormat.format(observableSection.asSection().xOfPicket(picket))
            }
        }
    }

    @FXML
    private fun closeFile(event: Event) {
        if (askToSave(event).isConsumed) {
            return
        }
        storageManager.resetSavedSnapshot()
        observableSection.restoreFromSnapshot(snapshotOf(Section()))
        historyManager.clear()
        resetWindowTitle()
    }

    private fun askToSave(event: Event): Event {
        if (storageManager.savedSnapshot != observableSection.snapshot()) {
            val saveDialog = saveDialogProvider.get()
            saveDialog.initOwner(stage)
            val answer = saveDialog.showAndWait()
            if (answer.isPresent) {
                if (answer.get() == ButtonType.YES) {
                    saveSection()
                } else if (answer.get() == ButtonType.CANCEL) {
                    event.consume()
                }
            }
        }
        return event
    }

    @FXML
    override fun importEXP() {
        val files = expFileChooser.showOpenMultipleDialog(stage)
        if (files != null) {
            if (files[files.size - 1].parentFile.isDirectory) {
                expFileChooser.initialDirectory = files[files.size - 1].parentFile
                preferences.put(EXP_FILES_DIR.key, files[files.size - 1].parentFile.absolutePath)
            }
            for (file in files) {
                importEXP(file)
            }
        }
    }

    override fun importEXP(file: File) {
        try {
            val picketFromEXPFile = storageManager.fromEXPFile(file)
            val violations = validator.validate(picketFromEXPFile)
            if (violations.isNotEmpty()) {
                alertsFactory.violationsAlert(violations, stage).show()
                return
            }
            historyManager.snapshotAfter { observableSection.pickets.add(picketFromEXPFile) }
            picketIndex = observableSection.pickets.lastIndex
        } catch (e: Exception) {
            alertsFactory.incorrectFileAlert(e, stage).show()
        }
    }

    @FXML
    override fun openJsonSection(event: Event) {
        if (askToSave(event).isConsumed) {
            return
        }
        val file = jsonFileChooser.showOpenDialog(stage)
        if (file != null) {
            if (file.parentFile.isDirectory) {
                jsonFileChooser.initialDirectory = file.parentFile
                preferences.put(JSON_FILES_DIR.key, file.parentFile.absolutePath)
            }
            openJsonSection(file)
        }
    }

    override fun openJsonSection(file: File) {
        try {
            val loadedSection = storageManager.loadFromJson(file, Section::class.java)
            val violations = validator.validate(loadedSection)
            if (violations.isNotEmpty()) {
                alertsFactory.violationsAlert(violations, stage).show()
                storageManager.resetSavedSnapshot()
                return
            }
            observableSection.restoreFromSnapshot(snapshotOf(loadedSection))
            picketIndex = 0
            historyManager.clear()
            historyManager.snapshot()
            setWindowFileTitle(file)
            preferences.put(
                RECENT_FILES.key, file.absolutePath
                        + File.pathSeparator
                        + preferences[RECENT_FILES.key, RECENT_FILES.def]
            )
        } catch (e: Exception) {
            alertsFactory.incorrectFileAlert(e, stage).show()
        }
    }

    @FXML
    private fun saveSection() {
        if (storageManager.savedSnapshot != observableSection.snapshot()) {
            saveSection(
                if (storageManager.savedSnapshotFile != null) {
                    storageManager.savedSnapshotFile
                } else {
                    jsonFileChooser.showSaveDialog(stage)
                }
            )
        }
    }

    @FXML
    private fun saveSectionAs() {
        saveSection(jsonFileChooser.showSaveDialog(stage))
    }

    /**
     * Opens new window
     */
    @FXML
    private fun newWindow() {
        mainViewProvider.get().show()
    }

    /**
     * Asks which file to import and then import it
     */
    @FXML
    override fun importMOD() {
        val file = modFileChooser.showOpenDialog(stage)
        if (file != null) {
            if (file.parentFile.isDirectory) {
                modFileChooser.initialDirectory = file.parentFile
                preferences.put(MOD_FILES_DIR.key, file.parentFile.absolutePath)
            }
            importMOD(file)
        }
    }

    override fun importMOD(file: File) {
        try {
            val newPicket = picket.copy(modelData = storageManager.fromMODFile(file))
            val violations = validator.validate(newPicket)
            if (violations.isNotEmpty()) {
                alertsFactory.violationsAlert(violations, stage)
                return
            }
            historyManager.snapshotAfter { observableSection.pickets[picketIndex] = newPicket }
        } catch (e: Exception) {
            alertsFactory.incorrectFileAlert(e, stage).show()
        }
    }

    override fun importJsonPicket() {
        val file = jsonFileChooser.showOpenDialog(stage)
        if (file != null) {
            if (file.parentFile.isDirectory) {
                jsonFileChooser.initialDirectory = file.parentFile
                preferences.put(JSON_FILES_DIR.key, file.parentFile.absolutePath)
            }
            importJsonPicket(file)
        }
    }

    override fun importJsonPicket(file: File) {
        try {
            val loadedPicket = storageManager.loadFromJson(file, Picket::class.java)
            historyManager.snapshotAfter { observableSection.pickets.add(loadedPicket) }
            picketIndex = observableSection.pickets.lastIndex
        } catch (e: Exception) {
            alertsFactory.incorrectFileAlert(e, stage).show()
        }
    }

    @FXML
    private fun exportJsonPicket() {
        val file = jsonFileChooser.showSaveDialog(stage)
        if (file != null) {
            if (file.parentFile.isDirectory) {
                jsonFileChooser.initialDirectory = file.parentFile
                preferences.put(JSON_FILES_DIR.key, file.parentFile.absolutePath)
            }
            try {
                storageManager.saveToJson(file, picket)
            } catch (e: Exception) {
                alertsFactory.simpleExceptionAlert(e, stage).show()
            }
        }
    }

    @FXML
    private fun switchToNextPicket() {
        if (picketIndex + 1 <= observableSection.pickets.lastIndex && !observableSection.pickets.isEmpty()) {
            picketIndex++
        }
    }

    @FXML
    private fun switchToPrevPicket() {
        if (picketIndex >= 1 && !observableSection.pickets.isEmpty()) {
            picketIndex--
        }
    }

    @FXML
    private fun inverseSolve() {
        try {
            historyManager.snapshotAfter {
                observableSection.pickets[picketIndex] =
                    picket.copy(modelData = inverseSolver.getOptimizedModelData(picket))
            }
        } catch (e: Exception) {
            alertsFactory.simpleExceptionAlert(e, stage).show()
        }
    }

    private fun saveSection(file: File?) {
        if (file != null) {
            if (file.parentFile.isDirectory) {
                jsonFileChooser.initialDirectory = file.parentFile
                preferences.put(JSON_FILES_DIR.key, file.parentFile.absolutePath)
                preferences.put(
                    RECENT_FILES.key, file.absolutePath
                            + File.pathSeparator
                            + preferences[RECENT_FILES.key, RECENT_FILES.def]
                )
            }
            try {
                storageManager.saveToJson(file, observableSection.snapshot().value)
                setWindowFileTitle(file)
                dirtyAsterisk.set("")
            } catch (e: Exception) {
                alertsFactory.incorrectFileAlert(e, stage).show()
            }
        }
    }

    @FXML
    private fun submitOffsetX() {
        val offsetX: Double = try {
            decimalFormat.parse(picketOffsetX.text).toDouble()
        } catch (e: ParseException) {
            alertsFactory.simpleExceptionAlert(e, stage).show()
            picketOffsetX.selectAll()
            return
        }
        val modified = picket.copy(offsetX = offsetX)
        val violations = validator.validate(modified)
        if (violations.isNotEmpty()) {
            alertsFactory.violationsAlert(violations, stage).show()
            picketOffsetX.selectAll()
        } else {
            historyManager.snapshotAfter { observableSection.pickets[picketIndex] = modified }
            FXUtils.unfocus(picketOffsetX)
        }
    }

    @FXML
    private fun submitPicketName() {
        historyManager.snapshotAfter {
            observableSection.pickets[picketIndex] = picket.copy(name = picketName.text)
        }
        FXUtils.unfocus(picketName)
    }

    @FXML
    private fun submitZ() {
        val z: Double = try {
            decimalFormat.parse(picketZ.text).toDouble()
        } catch (e: ParseException) {
            alertsFactory.simpleExceptionAlert(e, stage).show()
            picketZ.selectAll()
            return
        }
        val modified = picket.copy(z = z)
        val violations = validator.validate(modified)
        if (violations.isNotEmpty()) {
            alertsFactory.violationsAlert(violations, stage).show()
            picketZ.selectAll()
        } else {
            historyManager.snapshotAfter { observableSection.pickets[picketIndex] = modified }
            FXUtils.unfocus(picketZ)
        }
    }

    @FXML
    override fun addNewPicket() {
        historyManager.snapshotAfter { observableSection.pickets += Picket() }
        picketIndex = observableSection.pickets.lastIndex
    }

    @FXML
    private fun undo() {
        historyManager.undo()
    }

    @FXML
    private fun redo() {
        historyManager.redo()
    }

    private fun setWindowFileTitle(file: File) {
        windowTitle.set(file.name)
    }

    private fun resetWindowTitle() {
        windowTitle.set("GEM")
    }
}
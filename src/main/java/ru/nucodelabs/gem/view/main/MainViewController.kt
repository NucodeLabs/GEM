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
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Screen
import javafx.stage.Stage
import ru.nucodelabs.geo.ves.calc.inverse.InverseSolver
import ru.nucodelabs.gem.fxmodel.ObservableSection
import ru.nucodelabs.geo.ves.Picket
import ru.nucodelabs.geo.ves.Section
import ru.nucodelabs.geo.ves.calc.xOfPicket
import ru.nucodelabs.gem.app.io.StorageManager
import ru.nucodelabs.gem.app.pref.*
import ru.nucodelabs.gem.app.snapshot.HistoryManager
import ru.nucodelabs.gem.app.snapshot.snapshotOf
import ru.nucodelabs.gem.util.fx.*
import ru.nucodelabs.gem.util.fx.FXUtils
import ru.nucodelabs.gem.util.OS.macOS
import ru.nucodelabs.gem.util.fx.DoubleValidationConverter
import ru.nucodelabs.gem.util.fx.plus
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.AlertsFactory
import ru.nucodelabs.gem.view.charts.MisfitStacksController
import ru.nucodelabs.gem.view.charts.ModelSectionSwitcherController
import ru.nucodelabs.gem.view.charts.PseudoSectionSwitcherController
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
    @Named("CSS") private val stylesheet: String,
    private val picketObservable: ObservableObjectValue<Picket>,
    private val picketIndexProperty: IntegerProperty,
    private val observableSection: ObservableSection,
    private val historyManager: HistoryManager<Section>,
    private val alertsFactory: AlertsFactory,
    private val storageManager: StorageManager,
    private val validator: Validator,
    private val preferences: Preferences,
    private val decimalFormat: DecimalFormat,
    @FXML private val fxPreferences: FXPreferences,
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
    private lateinit var vesCurvesBox: VBox

    @FXML
    private lateinit var vesSectionSplitContainer: VBox

    @FXML
    private lateinit var vesSectionSplit: SplitPane

    @FXML
    private lateinit var sectionContainer: HBox

    @FXML
    private lateinit var sectionBox: VBox

    @FXML
    private lateinit var addExperimentalData: VBox

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
    private lateinit var menuViewGraphTitles: CheckMenuItem

    @FXML
    private lateinit var menuViewSectionInSeparateWindow: CheckMenuItem

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

    @FXML
    private lateinit var pseudoSectionSwitcherController: PseudoSectionSwitcherController

    @FXML
    private lateinit var modelSectionSwitcherController: ModelSectionSwitcherController

    @FXML
    private lateinit var commentTextArea: TextArea

    override val stage: Stage
        get() = root

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)

        stage.onCloseRequest = EventHandler { e ->
            if (!askToSave(e).isConsumed) {
                menuViewSectionInSeparateWindow.isSelected = false
            }
        }
//        stage.scene.accelerators[KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN)] = Runnable { redo() }
        macOS {
            val useSystemMenu = CheckMenuItem(resources["useSystemMenu"])
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
        setupInverseShortcut()
        setupMenuItems()
    }

    private fun setupInverseShortcut() {
        stage.scene.accelerators[KeyCodeCombination(KeyCode.SPACE)] = Runnable { inverseSolve() }
    }

    private fun setupTextFields() {
        picketOffsetX.textFormatter = TextFormatter(DoubleValidationConverter(decimalFormat), 100.0)
        picketZ.textFormatter = TextFormatter(DoubleValidationConverter(decimalFormat), 0.0)

        commentTextArea.focusedProperty().addListener { _, _, isFocused ->
            if (!isFocused) {
                if (observableSection.pickets.isNotEmpty()) {
                    historyManager.snapshotAfter {
                        observableSection.pickets[picketIndex] = picket.copy(comment = commentTextArea.text)
                    }
                }
            }
        }
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
            .coerceAtMost(Screen.getPrimary().bounds.width)
        stage.height = fxPreferences.bind(stage.heightProperty(), MAIN_WINDOW_H.key, MAIN_WINDOW_H.def)
            .coerceAtMost(Screen.getPrimary().bounds.height)
        stage.x = fxPreferences.bind(stage.xProperty(), MAIN_WINDOW_X.key, MAIN_WINDOW_X.def).coerceAtLeast(0.0)
        stage.y = fxPreferences.bind(stage.yProperty(), MAIN_WINDOW_Y.key, MAIN_WINDOW_Y.def).coerceAtLeast(0.0)
        fxPreferences.setAndBind(
            menuViewVESCurvesLegend.selectedProperty(),
            VES_CURVES_LEGEND_VISIBLE.key,
            VES_CURVES_LEGEND_VISIBLE.def
        )

        fxPreferences.setAndBind(menuViewGraphTitles.selectedProperty(), GRAPHS_TITLES.key, GRAPHS_TITLES.def)
    }

    private fun bind() {
        noFileScreenController.visibleProperty().bind(noFileOpenedProperty)

        vesNumberProperty.bind(
            Bindings.createStringBinding(
                { (picketIndex + 1).toString() + "/" + observableSection.pickets.size },
                picketIndexProperty, observableSection.pickets
            )
        )
        picketObservable.addListener { _: ObservableValue<out Picket?>?, _: Picket?, newValue: Picket? ->
            if (newValue != null) {
                picketName.text = newValue.name
                commentTextArea.text = newValue.comment
            } else {
                picketName.text = "-"
                commentTextArea.text = ""
            }
        }
        noFileOpenedProperty.bind(
            Bindings.createBooleanBinding(
                { observableSection.pickets.isEmpty() },
                observableSection.pickets
            )
        )
        observableSection.pickets.addListener(ListChangeListener {
            while (it.next()) {
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

    private fun setupMenuItems() {
        vesCurvesController.title.visibleProperty().bind(menuViewGraphTitles.selectedProperty())
        vesCurvesController.title.managedProperty().bind(menuViewGraphTitles.selectedProperty())

        modelSectionSwitcherController
            .logSectionBoxController.title.visibleProperty().bind(menuViewGraphTitles.selectedProperty())
        modelSectionSwitcherController
            .logSectionBoxController.title.managedProperty().bind(menuViewGraphTitles.selectedProperty())

        modelSectionSwitcherController
            .linearSectionBoxController.title.visibleProperty().bind(menuViewGraphTitles.selectedProperty())
        modelSectionSwitcherController
            .linearSectionBoxController.title.managedProperty().bind(menuViewGraphTitles.selectedProperty())

        pseudoSectionSwitcherController
            .curvesPseudoSectionBoxController.title.visibleProperty().bind(menuViewGraphTitles.selectedProperty())
        pseudoSectionSwitcherController
            .curvesPseudoSectionBoxController.title.managedProperty().bind(menuViewGraphTitles.selectedProperty())

        pseudoSectionSwitcherController
            .mapPseudoSectionBoxController.title.visibleProperty().bind(menuViewGraphTitles.selectedProperty())
        pseudoSectionSwitcherController
            .mapPseudoSectionBoxController.title.managedProperty().bind(menuViewGraphTitles.selectedProperty())

        vesCurvesController.legendVisibleProperty().bind(menuViewVESCurvesLegend.selectedProperty())

        noFileOpenedProperty.addListener { _, _, noFile ->
            if (noFile) {
                menuViewSectionInSeparateWindow.isSelected = false
            }
        }
        menuViewSectionInSeparateWindow.selectedProperty().addListener { _, _, isSelected ->
            if (isSelected) {
                Stage().apply {
                    icons.setAll(root.icons)
                    titleProperty().bind(stage.titleProperty() + " - Разрез")
                    onCloseRequest = EventHandler { menuViewSectionInSeparateWindow.isSelected = false }
                    prepareToSeparateSection()
                    scene = Scene(sectionBox).apply {
                        stylesheets += stylesheet
                    }
                }.show()
            } else {
                (sectionBox.scene.window as Stage).close()
                prepareToMergeSection()
            }
        }
    }

    private fun prepareToMergeSection() {
        vesSectionSplitContainer.children -= vesCurvesBox
        vesSectionSplit.items += vesCurvesBox
        vesSectionSplitContainer.children += vesSectionSplit
        sectionContainer.children += sectionBox
    }

    private fun prepareToSeparateSection() {
        sectionContainer.children -= sectionBox
        vesSectionSplitContainer.children -= vesSectionSplit
        vesSectionSplit.items -= vesCurvesBox
        vesSectionSplitContainer.children += vesCurvesBox
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

    @FXML
    private fun openAddExpData() {
        if (addExperimentalData.scene == null) {
            Stage().apply {
                title = "Добавить измерение"
                initOwner(this@MainViewController.stage)
                addExperimentalData.stylesheets += stylesheet
                scene = Scene(addExperimentalData)
                isResizable = false
            }.show()
        } else {
            (addExperimentalData.scene.window as Stage).show()
        }
    }

//    @FXML
//    private fun openAuthors() {
//        if (authorsWindow.owner == null) {
//            authorsWindow.initOwner(stage)
//        }
//        authorsWindow.icons.setAll(stage.icons)
//        authorsWindow.show()
//    }
}
package ru.nucodelabs.gem.view.main

import javafx.beans.property.IntegerProperty
import javafx.beans.value.ObservableObjectValue
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.layout.HBox
import javafx.stage.Stage
import ru.nucodelabs.data.ves.Section
import ru.nucodelabs.data.ves.length
import ru.nucodelabs.data.ves.picketsWidths
import ru.nucodelabs.gem.app.model.SectionManager
import ru.nucodelabs.gem.app.snapshot.HistoryManager
import ru.nucodelabs.gem.view.AbstractController
import java.net.URL
import java.util.*
import javax.inject.Inject

class PicketsBarController @Inject constructor(
    private val sectionObservable: ObservableObjectValue<Section>,
    private val picketIndex: IntegerProperty,
    private val sectionManager: SectionManager,
    private val historyManager: HistoryManager<Section>
) : AbstractController() {
    @FXML
    private lateinit var container: HBox

    override val stage: Stage?
        get() = container.scene.window as Stage?

    private val section: Section
        get() = sectionObservable.get()!!

    override fun initialize(location: URL, resources: ResourceBundle) {
        sectionObservable.addListener { _, _, newValue: Section? ->
            newValue?.let { update() }
        }
        picketIndex.addListener { _, _, _ -> update() }
    }

    private fun update() {
        val pickets = section.pickets

        val buttons = mutableListOf<Button>()

        val widthsOfPickets = section.picketsWidths()
        for ((index, picket) in pickets.withIndex()) {
            // TODO использовать UI Properties
            val contextMenu = ContextMenu(
                MenuItem("Переместить влево").apply {
                    onAction = EventHandler {
                        historyManager.snapshotAfter {
                            sectionManager.swap(index, index - 1)
                        }
                    }
                    if (index == 0) {
                        isDisable = true
                    }
                },
                MenuItem("Переместить вправо").apply {
                    onAction = EventHandler {
                        historyManager.snapshotAfter {
                            sectionManager.swap(index, index + 1)
                        }
                    }
                    if (index == pickets.lastIndex) {
                        isDisable = true
                    }
                },
                MenuItem("Удалить").apply {
                    onAction = EventHandler {
                        historyManager.snapshotAfter {
                            sectionManager.remove(index)
                        }
                    }
                    if (pickets.size == 1) {
                        isDisable = true
                    }
                },
            )

            val button = Button(picket.name).apply {
                if (index == picketIndex.get()) {
                    style = "-fx-background-color: LightGray;"
                }
                onAction = EventHandler { picketIndex.set(index) }
                onContextMenuRequested = EventHandler { contextMenu.show(stage, it.screenX, it.screenY) }
                prefWidthProperty().bind(
                    container.widthProperty()
                        .multiply(
                            if (section.length() > 0) {
                                widthsOfPickets[index] / section.length()
                            } else {
                                1.0
                            }
                        )
                )
            }

            buttons += button
        }
        container.children.setAll(buttons)
    }
}
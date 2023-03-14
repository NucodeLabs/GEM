package ru.nucodelabs.gem.view.controller

import javafx.event.Event
import java.io.File

interface FileOpener {
    fun openJsonSection(event: Event)
    fun openJsonSection(file: File)
    fun addNewPicket()
}
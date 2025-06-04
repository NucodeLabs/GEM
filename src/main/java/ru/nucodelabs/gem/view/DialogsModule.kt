package ru.nucodelabs.gem.view

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.name.Named
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog

class DialogsModule : AbstractModule() {
    @Provides
    @Named("Save")
    fun provideSaveDialog(@Named("CSS") stylesheet: String): Dialog<ButtonType> =
        Dialog<ButtonType>().apply {
            title = "Сохранение"
            contentText = "Сохранить изменения?"
            dialogPane.buttonTypes.addAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL)
            dialogPane.stylesheets.add(stylesheet)
        }
}

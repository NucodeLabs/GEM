package ru.nucodelabs.gem.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class DialogsModule extends AbstractModule {

    @Provides
    @Named(Name.SAVE)
    Dialog<ButtonType> provideSaveDialog() {
        Dialog<ButtonType> saveDialog = new Dialog<>();
        saveDialog.setTitle("Сохранение");
        saveDialog.setContentText("Сохранить изменения?");
        saveDialog.getDialogPane().getButtonTypes()
                .addAll(ButtonType.YES,
                        ButtonType.NO,
                        ButtonType.CANCEL);
        saveDialog.getDialogPane().getStylesheets().add(Style.COMMON_STYLESHEET);
        return saveDialog;
    }
}

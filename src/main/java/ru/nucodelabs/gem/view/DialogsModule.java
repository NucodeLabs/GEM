package ru.nucodelabs.gem.view;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class DialogsModule extends AbstractModule {
    @Provides
    @Named("Save")
    private Dialog<ButtonType> provideSaveDialog(@Named("CSS") String stylesheet) {
        Dialog<ButtonType> saveDialog = new Dialog<>();
        saveDialog.setTitle("Сохранение");
        saveDialog.setContentText("Сохранить изменения?");
        saveDialog.getDialogPane().getButtonTypes()
                .addAll(ButtonType.YES,
                        ButtonType.NO,
                        ButtonType.CANCEL);
        saveDialog.getDialogPane().getStylesheets().add(stylesheet);
        return saveDialog;
    }
}

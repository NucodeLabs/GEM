package ru.nucodelabs.mvvm;

import javafx.stage.Stage;

import java.util.Objects;

/**
 * Abstract class of views that have VBox as root container.
 * You must create same named FXML-file in same package in resources folder.
 *
 * @param <VM> view model that belongs to this view
 */
public abstract class VBView<VM extends ViewModel> extends VBUserControl {

    protected VM viewModel;

    public VBView(VM viewModel) {
        this.viewModel = viewModel;
        Initializers.addCloseShortcutMacOS(this);
    }

    protected void selfClose() {
        Stage stage = (Stage) this.getScene().getWindow();
        Objects.requireNonNull(stage);
        stage.close();
    }
}

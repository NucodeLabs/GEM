package ru.nucodelabs.gem.view.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import ru.nucodelabs.mvvm.VBView;

public class ImportOptionsPrompt extends VBView<MainViewModel> {
    @FXML
    public Button addToCurrentBtn;
    @FXML
    public Button addToNewBtn;

    public ImportOptionsPrompt(MainViewModel viewModel) {
        super(viewModel);
        addToNewBtn.setOnAction(e -> {
            viewModel.addToNew();
            ((Stage) this.getScene().getWindow()).close();
        });
        addToCurrentBtn.setOnAction(e -> {
            viewModel.addToNew();
            ((Stage) this.getScene().getWindow()).close();
        });
    }
}

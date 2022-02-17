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
    @FXML
    public Button cancelBtn;

    public ImportOptionsPrompt(MainViewModel viewModel) {
        super(viewModel);
        addToNewBtn.setOnAction(e -> {
            viewModel.addToNew();
            ((Stage) this.getScene().getWindow()).close();
        });
        addToCurrentBtn.setOnAction(e -> {
            viewModel.addToCurrent();
            ((Stage) this.getScene().getWindow()).close();
        });
        cancelBtn.setOnAction(e -> ((Stage) this.getScene().getWindow()).close());
    }
}

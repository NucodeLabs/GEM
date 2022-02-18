package ru.nucodelabs.gem.view.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
            selfClose();
        });
        addToCurrentBtn.setOnAction(e -> {
            viewModel.addToCurrent();
            selfClose();
        });
        cancelBtn.setOnAction(e -> selfClose());
    }
}

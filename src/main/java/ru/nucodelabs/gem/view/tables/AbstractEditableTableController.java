package ru.nucodelabs.gem.view.tables;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import ru.nucodelabs.gem.view.AbstractController;

import java.util.List;

import static ru.nucodelabs.gem.view.tables.Tables.validateDataInput;
import static ru.nucodelabs.gem.view.tables.Tables.validateIndexInput;

public abstract class AbstractEditableTableController extends AbstractController {

    abstract protected List<TextField> getRequiredForAdd();

    abstract protected Button getAddButton();

    protected void addDataInputCheckListener(TextField dataTextField) {
        dataTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            dataTextField.getStyleClass().remove("wrong-input");
            getAddButton().setDisable(false);
            if (!validateDataInput(newValue)) {
                dataTextField.getStyleClass().add("wrong-input");
                getAddButton().setDisable(true);
            } else {
                if (!getRequiredForAdd().stream()
                        .allMatch(textField ->
                                !textField.getText().isBlank()
                                        && validateDataInput(textField.getText()))) {
                    getAddButton().setDisable(true);
                }
            }
        });
    }

    protected void addEnterKeyHandler(TextField textField) {
        textField.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ENTER
                    && !getAddButton().isDisabled()) {
                getAddButton().fire();
            }
        });
    }

    protected void addIndexInputCheckListener(TextField indexTextField) {
        indexTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            indexTextField.getStyleClass().remove("wrong-input");
            getAddButton().setDisable(false);
            if (!validateIndexInput(newValue)) {
                indexTextField.getStyleClass().add("wrong-input");
                getAddButton().setDisable(true);
            } else {
                if (!getRequiredForAdd().stream()
                        .allMatch(textField ->
                                !textField.getText().isBlank()
                                        && validateDataInput(textField.getText()))) {
                    getAddButton().setDisable(true);
                }
            }
        });
    }
}

package ru.nucodelabs.gem.view.tables;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import ru.nucodelabs.gem.utils.FXUtils;
import ru.nucodelabs.gem.view.AbstractController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractEditableTableController extends AbstractController {

    protected <T> List<T> deleteIndices(List<Integer> indicesToRemove, List<T> removeFrom) {
        indicesToRemove = indicesToRemove.stream().sorted(Comparator.reverseOrder()).toList();
        List<T> list = new ArrayList<>(removeFrom);
        indicesToRemove.forEach(i -> list.remove(i.intValue()));
        return list;
    }

    abstract protected List<TextField> getRequiredForAdd();

    abstract protected Button getAddButton();

    protected void addEnterKeyHandler(TextField textField) {
        textField.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ENTER
                    && !getAddButton().isDisabled()) {
                getAddButton().fire();
            }
        });
    }

    protected void addValidationListener(
            TextField indexTextField,
            Predicate<String> validateInput) {
        FXUtils.addValidationListener(
                indexTextField,
                validateInput,
                () -> getAddButton().setDisable(true),
                () -> getAddButton().setDisable(false),
                "-fx-background-color: LightPink;", getRequiredForAdd()
        );
    }
}

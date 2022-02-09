package ru.nucodelabs.mvvm;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import ru.nucodelabs.gem.core.utils.OSDetector;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Initializers {
    /**
     * Load FXML file with name equals to {@code root} class name and sets the {@code root} as the controller of loaded FXML.
     *
     * @param root object that inherits class of root node of FXML
     * @param <N>  class of root
     */
    public static <N extends Node> void initFXMLControl(N root) {
        ResourceBundle bundle = ResourceBundle.getBundle("ru/nucodelabs/gem/UI", new Locale("ru"));
        String fxmlFileName = root.getClass().getSimpleName() + ".fxml";
        FXMLLoader loader = new FXMLLoader(root.getClass().getResource(fxmlFileName), bundle);
        loader.setRoot(root);
        loader.setController(root);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <N extends Node> void addCloseShortcutMacOS(N root) {
        if (new OSDetector().isMacOS()) {
            root.addEventFilter(KeyEvent.KEY_PRESSED,
                    e -> {
                        KeyCodeCombination closeShortcut = new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN);
                        if (closeShortcut.match(e)) {
                            ((Stage) root.getScene().getWindow()).close();
                        }
                    }
            );
        }
    }
}

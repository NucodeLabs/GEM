package ru.nucodelabs.gem.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Static methods for initializing user controls and view
 */
public class FXUtils {

    private FXUtils() {
    }

    /**
     * Load FXML file with name equals to {@code root} class name and sets the {@code root} as the controller of loaded FXML.
     *
     * @param root   object that inherits class of root node of FXML
     * @param <N>    class of root
     * @param locale locale
     */
    public static <N extends Node> void initFXMLControl(N root, Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("ru/nucodelabs/gem/UI", locale);
        initFXMLControl(root, bundle);
    }

    public static <N extends Node> void initFXMLControl(N root) {
        ResourceBundle bundle = ResourceBundle.getBundle("ru/nucodelabs/gem/UI");
        initFXMLControl(root, bundle);
    }

    private static <N extends Node> void initFXMLControl(N root, ResourceBundle bundle) {
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

    /**
     * Adds ⌘W shortcut that must be supported on macOS
     *
     * @param root root node
     * @param <N>  class of root node
     */
    public static <N extends Node> void addCloseShortcutMacOS(N root) {
        if (OSDetect.isMacOS()) {
            root.addEventFilter(KeyEvent.KEY_PRESSED,
                    e -> {
                        KeyCodeCombination closeShortcut = new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN);
                        if (closeShortcut.match(e)) {
                            Window window = root.getScene().getWindow();
                            window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
                        }
                    }
            );
        }
    }
}

package ru.nucodelabs.gem.utils;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;

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
        if (OS.isMacOS()) {
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

    public static void unfocus(Node... nodes) {
        for (var node : nodes) {
            if (node.isFocused()) {
                node.getParent().requestFocus();
            }
        }
    }

    public static BooleanBinding isBlank(StringProperty stringProperty) {
        return Bindings.createBooleanBinding(() -> stringProperty.get().isBlank(), stringProperty);
    }

    public static BooleanBinding isNotBlank(StringProperty stringProperty) {
        return isBlank(stringProperty).not();
    }

    public static class TextFieldValidationSetup {
        private final TextField textField;

        private Runnable ifValid = () -> {
        };

        private Runnable ifInvalid = () -> {
        };

        private String invalidStyle = "";

        private Predicate<String> validateInputString = s -> true;

        private TextFieldValidationSetup(TextField textField) {
            this.textField = textField;
        }

        public static TextFieldValidationSetup of(TextField textField) {
            return new TextFieldValidationSetup(textField);
        }

        public TextFieldValidationSetup doIfValid(Runnable ifValid) {
            this.ifValid = ifValid;
            return this;
        }

        public TextFieldValidationSetup doIfInvalid(Runnable ifInvalid) {
            this.ifInvalid = ifInvalid;
            return this;
        }

        public TextFieldValidationSetup applyStyleIfInvalid(String invalidStyle) {
            this.invalidStyle = invalidStyle;
            return this;
        }

        public TextFieldValidationSetup validateWith(Predicate<String> validateInputString) {
            this.validateInputString = validateInputString;
            return this;
        }

        public BooleanProperty done() {

            BooleanProperty valid = initProperty();

            textField.textProperty().addListener((observable, oldValue, newValue) -> validate(valid, newValue));

            return valid;
        }

        private BooleanProperty initProperty() {
            BooleanProperty valid = new SimpleBooleanProperty();
            String initialValue = textField.getText();

            validate(valid, initialValue);
            return valid;
        }

        private void validate(BooleanProperty valid, String input) {
            if (!validateInputString.test(input)) {
                valid.set(false);
                textField.setStyle(invalidStyle);
                ifInvalid.run();
            } else {
                valid.set(true);
                textField.setStyle("");
                ifValid.run();
            }
        }
    }

    /**
     * @return "rgba(%d, %d, %d, %f)"
     */
    public static String toWeb(Color color) {
        return String.format("rgba(%d, %d, %d, %f)",
                (int) Math.ceil(color.getRed() * 255),
                (int) Math.ceil(color.getGreen() * 255),
                (int) Math.ceil(color.getBlue() * 255),
                color.getOpacity());
    }
}

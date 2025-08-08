package ru.nucodelabs.gem.view.control;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Static methods for initializing user controls and view
 */
public class FxmlUtil {

    private FxmlUtil() {
    }

    /**
     * Load FXML file with name equals to {@code root} class name and sets the {@code root} as the controller of loaded FXML.
     *
     * @param root   object that inherits class of root node of FXML
     * @param <N>    class of root
     * @param locale locale
     */
    public static <N extends Node> void initFxmlControl(N root, Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("ru/nucodelabs/gem/UI", locale);
        initFxmlControl(root, bundle);
    }

    public static <N extends Node> void initFxmlControl(N root) {
        ResourceBundle bundle = ResourceBundle.getBundle("ru/nucodelabs/gem/UI");
        initFxmlControl(root, bundle);
    }

    private static <N extends Node> void initFxmlControl(N root, ResourceBundle bundle) {
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
}

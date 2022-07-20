package ru.nucodelabs.gem.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.paint.Color;

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

    public static void unfocus(Node... nodes) {
        for (var node : nodes) {
            if (node.isFocused()) {
                node.getParent().requestFocus();
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

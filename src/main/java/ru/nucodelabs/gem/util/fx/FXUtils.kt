package ru.nucodelabs.gem.util.fx

import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.paint.Color
import java.io.IOException
import java.util.*

/**
 * Utility functions for initializing controls and manipulating colors.
 */
object FXUtils {

    @JvmStatic
    fun <N : Node> initFXMLControl(root: N, locale: Locale = Locale.getDefault()) {
        val bundle = ResourceBundle.getBundle("ru/nucodelabs/gem/UI", locale)
        val fxmlFileName = root.javaClass.simpleName + ".fxml"
        val loader = FXMLLoader(root.javaClass.getResource(fxmlFileName), bundle)
        loader.setRoot(root)
        loader.setController(root)
        try {
            loader.load<Any>()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun unfocus(vararg nodes: Node) {
        nodes.filter(Node::isFocused).forEach { it.parent.requestFocus() }
    }

    /**
     * @return string in format "rgba(%d, %d, %d, %f)".
     */
    @JvmStatic
    fun toWeb(color: Color): String = with(color) {
        String.format(
            Locale.US,
            "rgba(%d, %d, %d, %f)",
            kotlin.math.round(red * 255).toInt(),
            kotlin.math.round(green * 255).toInt(),
            kotlin.math.round(blue * 255).toInt(),
            opacity
        )
    }
}

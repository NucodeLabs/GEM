package ru.nucodelabs.gem.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.google.inject.AbstractModule
import com.google.inject.Injector
import com.google.inject.Provides
import com.google.inject.Singleton
import com.google.inject.name.Named
import jakarta.validation.Validation
import jakarta.validation.Validator
import javafx.fxml.FXMLLoader
import javafx.stage.Stage
import javafx.util.StringConverter
import ru.nucodelabs.geo.ves.calc.forward.ForwardSolver
import ru.nucodelabs.files.clr.ClrParser
import ru.nucodelabs.files.clr.ColorNode
import ru.nucodelabs.gem.app.io.JacksonJsonFileManager
import ru.nucodelabs.gem.app.io.JsonFileManager
import ru.nucodelabs.gem.app.io.SonetImportManager
import ru.nucodelabs.gem.view.FileChoosersModule
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.gem.view.color.ColorPalette
import ru.nucodelabs.gem.view.main.MainViewController
import ru.nucodelabs.gem.view.main.MainViewModule
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.math.RoundingMode
import java.net.URL
import java.nio.file.Paths
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.util.*
import java.util.prefs.Preferences

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

/**
 * Application dependencies configuration.
 */
class AppModule : AbstractModule() {
    override fun configure() {
        install(FileChoosersModule())
    }

    @Provides
    @Singleton
    private fun provideUIProperties(): ResourceBundle =
        ResourceBundle.getBundle("ru/nucodelabs/gem/UI", Locale("ru"))

    @Provides
    @Named("CSS")
    @Singleton
    private fun provideStylesheet(): String = "ru/nucodelabs/gem/view/common.css"

    @Provides
    @Named("MainView")
    private fun provideMainViewFXML(): URL =
        MainViewController::class.java.getResource("MainSplitLayoutView.fxml")!!

    @Provides
    @Named("MainView")
    private fun provideFXMLLoader(
        uiProperties: ResourceBundle,
        injector: Injector,
        @Named("MainView") url: URL
    ): FXMLLoader = FXMLLoader(url, uiProperties).apply {
        controllerFactory = injector.createChildInjector(MainViewModule())::getInstance
    }

    @Provides
    @Named("MainView")
    @Throws(IOException::class)
    private fun create(@Named("MainView") loader: FXMLLoader): Stage = loader.load()

    @Provides
    private fun provideValidator(): Validator =
        Validation.buildDefaultValidatorFactory().use { it.validator }

    @Provides
    @Singleton
    private fun provideJsonFileManager(objectMapper: ObjectMapper): JsonFileManager =
        JacksonJsonFileManager(objectMapper)

    @Provides
    @Singleton
    private fun provideSonetImportManager(): SonetImportManager = SonetImportManager.create()

    @Provides
    private fun preferences(): Preferences = Preferences.userNodeForPackage(GemApplication::class.java)

    @Provides
    @Named("Precise")
    fun preciseFormat(): DecimalFormat = DecimalFormat().apply {
        roundingMode = RoundingMode.HALF_UP
        maximumFractionDigits = 16
        decimalFormatSymbols = DecimalFormatSymbols().apply {
            decimalSeparator = '.'
            groupingSeparator = ' '
        }
    }

    @Provides
    fun decimalFormat(): DecimalFormat = DecimalFormat().apply {
        roundingMode = RoundingMode.HALF_UP
        maximumFractionDigits = 2
        groupingSize = 3
        decimalFormatSymbols = DecimalFormatSymbols().apply {
            decimalSeparator = '.'
            groupingSeparator = ' '
        }
    }

    @Provides
    @Singleton
    fun doubleStringConverter(decimalFormat: DecimalFormat): StringConverter<Double> = object : StringConverter<Double>() {
        override fun toString(obj: Double?): String = try {
            decimalFormat.format(obj)
        } catch (e: Exception) {
            ""
        }

        override fun fromString(string: String?): Double = try {
            decimalFormat.parse(string).toDouble()
        } catch (e: ParseException) {
            throw RuntimeException(e)
        }
    }

    @Provides
    @Singleton
    fun numberStringConverter(doubleStringConverter: StringConverter<Double>): StringConverter<Number> =
        object : StringConverter<Number>() {
            override fun toString(obj: Number?): String =
                doubleStringConverter.toString(obj?.toDouble())

            override fun fromString(string: String?): Number =
                doubleStringConverter.fromString(string)
        }

    @Provides
    @Named("CLR")
    fun clrFile(): File = Paths.get("colormap/default.clr").toFile()

    @Provides
    @Singleton
    @Throws(FileNotFoundException::class)
    fun colorMapper(@Named("CLR") clrFile: File): ColorMapper {
        val clrParser = ClrParser(clrFile)
        println(clrFile.absolutePath)
        val valueColorList: List<ColorNode> = clrParser.colorNodes
        return ColorPalette(valueColorList, 0.0, 1500.0, 15)
    }

    @Provides
    @Singleton
    fun forwardSolver(): ForwardSolver = ForwardSolver.createDefault()

    @Provides
    fun objectMapper(): ObjectMapper =
        jacksonObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
}

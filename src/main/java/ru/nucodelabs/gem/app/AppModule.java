package ru.nucodelabs.gem.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ru.nucodelabs.geo.ves.calc.forward.ForwardSolver;
import ru.nucodelabs.files.clr.ClrParser;
import ru.nucodelabs.files.clr.ColorNode;
import ru.nucodelabs.gem.app.io.JacksonJsonFileManager;
import ru.nucodelabs.gem.app.io.JsonFileManager;
import ru.nucodelabs.gem.app.io.SonetImportManager;
import ru.nucodelabs.gem.view.FileChoosersModule;
import ru.nucodelabs.gem.view.color.ColorMapper;
import ru.nucodelabs.gem.view.color.ColorPalette;
import ru.nucodelabs.gem.view.main.MainViewController;
import ru.nucodelabs.gem.view.main.MainViewModule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static com.fasterxml.jackson.module.kotlin.ExtensionsKt.jacksonObjectMapper;

/**
 * Зависимости приложения, которое, по сути, создает MainView
 */
public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FileChoosersModule());
    }

    @Provides
    @Singleton
    private ResourceBundle provideUIProperties() {
        return ResourceBundle.getBundle("ru/nucodelabs/gem/UI", new Locale("ru"));
    }

    @Provides
    @Named("CSS")
    @Singleton
    private String provideStylesheet() {
        return "ru/nucodelabs/gem/view/common.css";
    }

    @Provides
    @Named("MainView")
    private URL provideMainViewFXML() {
        return MainViewController.class.getResource("MainSplitLayoutView.fxml");
    }

    @Provides
    @Named("MainView")
    private FXMLLoader provideFXMLLoader(ResourceBundle uiProperties, Injector injector, @Named("MainView") URL url) {
        FXMLLoader fxmlLoader = new FXMLLoader(url, uiProperties);
        fxmlLoader.setControllerFactory(injector.createChildInjector(new MainViewModule())::getInstance);
        return fxmlLoader;
    }

    @Provides
    @Named("MainView")
    private Stage create(@Named("MainView") FXMLLoader loader) throws IOException {
        return loader.load();
    }

    @Provides
    private Validator provideValidator() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator();
        }
    }

    @Provides
    @Singleton
    private JsonFileManager provideJsonFileManager(ObjectMapper objectMapper) {
        return new JacksonJsonFileManager(objectMapper);
    }

    @Provides
    @Singleton
    private SonetImportManager provideSonetImportManager() {
        return SonetImportManager.create();
    }

    @Provides
    private Preferences preferences() {
        return Preferences.userNodeForPackage(GemApplication.class);
    }

    @Provides
    @Named("Precise")
    DecimalFormat preciseFormat() {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        decimalFormat.setMaximumFractionDigits(16);
        var dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        dfs.setGroupingSeparator(' ');
        decimalFormat.setDecimalFormatSymbols(dfs);

        return decimalFormat;
    }

    @Provides
    private DecimalFormat decimalFormat() {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setGroupingSize(3);
        var dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        dfs.setGroupingSeparator(' ');
        decimalFormat.setDecimalFormatSymbols(dfs);

        return decimalFormat;
    }

    @Provides
    @Singleton
    private StringConverter<Double> doubleStringConverter(DecimalFormat decimalFormat) {
        return new StringConverter<>() {
            @Override
            public String toString(Double object) {
                try {
                    return decimalFormat.format(object);
                } catch (Exception e) {
                    return "";
                }
            }

            @Override
            public Double fromString(String string) {
                try {
                    return decimalFormat.parse(string).doubleValue();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Provides
    @Singleton
    StringConverter<Number> numberStringConverter(StringConverter<Double> doubleStringConverter) {
        return new StringConverter<>() {
            @Override
            public String toString(Number object) {
                return doubleStringConverter.toString(object.doubleValue());
            }

            @Override
            public Number fromString(String string) {
                return doubleStringConverter.fromString(string);
            }
        };
    }

    @Provides
    @Named("CLR")
    File clrFile() {
        return Paths.get("colormap/default.clr").toFile();
    }

    @Provides
    @Singleton
    ColorMapper colorMapper(@Named("CLR") File clrFile) throws FileNotFoundException {
        ClrParser clrParser = new ClrParser(clrFile);
        System.out.println(clrFile.getAbsolutePath());
        List<ColorNode> valueColorList = clrParser.getColorNodes();
        return new ColorPalette(valueColorList, 0, 1500, 15);
    }

    @Provides
    @Singleton
    ForwardSolver forwardSolver() {
        return ForwardSolver.createDefault();
    }

    @Provides
    ObjectMapper objectMapper() {
        return jacksonObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }
}

package ru.nucodelabs.gem.app;

import com.google.inject.*;
import com.google.inject.name.Named;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ru.nucodelabs.files.color_palette.CLRData;
import ru.nucodelabs.files.color_palette.CLRFileParser;
import ru.nucodelabs.gem.app.io.JsonFileManager;
import ru.nucodelabs.gem.app.io.SonetImportManager;
import ru.nucodelabs.gem.view.FileChoosersModule;
import ru.nucodelabs.gem.view.color_palette.ColorPalette;
import ru.nucodelabs.gem.view.color_palette.ColorPaletteController;
import ru.nucodelabs.gem.view.main.MainViewController;
import ru.nucodelabs.gem.view.main.MainViewModule;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

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
        return Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Provides
    @Singleton
    private JsonFileManager provideJsonFileManager() {
        return JsonFileManager.createDefault();
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
    @Singleton
    private DecimalFormat decimalFormat() {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setGroupingSize(3);
        var dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator(',');
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
                } catch (IllegalArgumentException e) {
                    return "";
                }
            }

            @Override
            public Double fromString(String string) {
                try {
                    return decimalFormat.parse(string).doubleValue();
                } catch (ParseException e) {
                    return Double.NaN;
                }
            }
        };
    }

    @Provides
    @Inject
    @Singleton
    public ObjectProperty<ColorPalette> provideColorPalette() throws Exception {
        return new SimpleObjectProperty<>(new ColorPalette(
                new CLRFileParser(new File("../GEM/data/clr/002_ERT_Rainbow_2.clr")).parse()));
    }
    /*@Provides
    ObjectProperty<CLRData> provideCLRPallete() throws Exception {
        return new SimpleObjectProperty<>(
                new CLRFileParser(new File("../GEM/data/clr/002_ERT_Rainbow_2.clr")).parse());
    }*/
}

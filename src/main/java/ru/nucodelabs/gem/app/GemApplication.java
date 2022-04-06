package ru.nucodelabs.gem.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.app.io.JsonFileManager;
import ru.nucodelabs.gem.view.main.MainViewController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Приложение, создает главное окошко
 */
public class GemApplication extends Application {

    private final Injector injector = Guice.createInjector(new AppModule());
    private final Logger logger = injector.getInstance(Logger.class);

    @Override
    public void start(Stage stage) {

        List<String> params = new ArrayList<>(getParameters().getRaw());
        params.addAll(StartGemApplication.macOSHandledFiles);

        if (!params.isEmpty()) {
            processParams(params);
        } else {
            logger.log(Level.INFO, "Starting MainView without parameters");
            injector.getInstance(Key.get(Stage.class, Names.named("MainView"))).show();
        }
    }

    private void processParams(List<String> params) {
        logger.log(Level.INFO, "Parameters are " + params);
        FXMLLoader fxmlLoader = injector.getInstance(Key.get(FXMLLoader.class, Names.named("MainView")));
        List<File> expFiles = new ArrayList<>();

        MainViewController controller = fxmlLoader.getController();
        for (var param : params) {
            if (param.endsWith(".EXP") || param.endsWith(".exp")) {
                logger.log(Level.INFO, "Add EXP, file: " + param);
                expFiles.add(new File(param));
            } else if (param.endsWith("json") || param.endsWith(".JSON")) {
                FXMLLoader fxmlLoader1 = injector.getInstance(Key.get(FXMLLoader.class, Names.named("MainView")));
                try {
                    ((Stage) fxmlLoader1.load()).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MainViewController controller1 = fxmlLoader1.getController();
                JsonFileManager jsonFileManager = injector.getInstance(JsonFileManager.class);
                File jsonFile = new File(param);
                try {
                    jsonFileManager.loadFromJson(jsonFile, Section.class);
                    logger.log(Level.INFO, "Open JSON Section, file: " + param);
                    controller1.openJsonSection(jsonFile);
                } catch (Exception e) {
                    logger.log(Level.WARNING, e.getMessage());
                    logger.log(Level.INFO, "Import JSON Picket, file: " + param);
                    controller1.importJsonPicket(jsonFile);
                }
            }
        }

        if (!expFiles.isEmpty()) {
            expFiles.forEach(controller::addEXP);
            try {
                ((Stage) fxmlLoader.load()).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

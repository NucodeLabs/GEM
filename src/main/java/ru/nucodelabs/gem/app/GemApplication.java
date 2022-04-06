package ru.nucodelabs.gem.app;

import com.google.inject.Guice;
import com.google.inject.Inject;
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

    @Inject
    private Logger logger;
    @Inject
    private JsonFileManager jsonFileManager;

    @Override
    public void start(Stage stage) {
        injector.injectMembers(this);

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

        List<File> expFiles = new ArrayList<>();

        for (var param : params) {
            if (param.endsWith(".EXP") || param.endsWith(".exp")) {
                logger.log(Level.INFO, "Import EXP, file: " + param);
                expFiles.add(new File(param));
            } else if (param.endsWith("json") || param.endsWith(".JSON")) {
                loadMainViewWithJSONFile(new File(param));
            }
        }

        if (!expFiles.isEmpty()) {
            loadMainViewWithEXPFiles(expFiles);
        }
    }

    private void loadMainViewWithJSONFile(File jsonFile) {
        FXMLLoader fxmlLoader = injector.getInstance(Key.get(FXMLLoader.class, Names.named("MainView")));
        try {
            ((Stage) fxmlLoader.load()).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MainViewController controller = fxmlLoader.getController();
        try {
            jsonFileManager.loadFromJson(jsonFile, Section.class);
            logger.log(Level.INFO, "Open JSON Section, file: " + jsonFile.getAbsolutePath());
            controller.openJsonSection(jsonFile);
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage());
            logger.log(Level.INFO, "Import JSON Picket, file: " + jsonFile.getAbsolutePath());
            controller.importJsonPicket(jsonFile);
        }
    }

    private void loadMainViewWithEXPFiles(List<File> expFiles) {
        FXMLLoader fxmlLoader = injector.getInstance(Key.get(FXMLLoader.class, Names.named("MainView")));
        try {
            ((Stage) fxmlLoader.load()).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MainViewController controller = fxmlLoader.getController();
        expFiles.forEach(controller::addEXP);
    }
}

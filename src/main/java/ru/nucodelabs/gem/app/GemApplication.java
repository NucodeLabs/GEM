package ru.nucodelabs.gem.app;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import ru.nucodelabs.gem.utils.OSDetect;
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

    private final List<String> macOSHandledFiles = new ArrayList<>();

    private final Injector injector = Guice.createInjector(new AppModule());

    @Inject
    private Logger logger;

    {
        if (OSDetect.isMacOS()) {
            com.sun.glass.ui.Application macOSSpecificApp = com.sun.glass.ui.Application.GetApplication();
            macOSSpecificApp.setEventHandler(new com.sun.glass.ui.Application.EventHandler() {
                @Override
                public void handleOpenFilesAction(com.sun.glass.ui.Application app, long time, String[] files) {
                    macOSHandledFiles.addAll(List.of(files));
                }
            });
        }
    }

    @Override
    public void init() throws Exception {
        injector.injectMembers(this);
        logger.log(Level.INFO, "Injected");
    }

    @Override
    public void start(Stage stage) throws Exception {

        List<String> params = new ArrayList<>(getParameters().getRaw());
        params.addAll(macOSHandledFiles);

        if (!params.isEmpty()) {
            processParams(params);
        } else {
            logger.log(Level.INFO, "Starting MainView without parameters");
            injector.getInstance(Key.get(Stage.class, Names.named("MainView"))).show();
        }
    }

    @Override
    public void stop() throws Exception {
        logger.log(Level.INFO, "Exiting");
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
        logger.log(Level.INFO, "Open JSON Section, file: " + jsonFile.getAbsolutePath());
        controller.openJsonSection(jsonFile);
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

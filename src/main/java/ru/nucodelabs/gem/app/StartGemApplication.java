package ru.nucodelabs.gem.app;

import javafx.application.Application;
import ru.nucodelabs.gem.utils.OSDetect;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main, запускает приложение JavaFX
 */
public class StartGemApplication {

    public static final List<String> macOSHandledFiles = new ArrayList<>();

    public static void main(String[] args) {
        // only for debug!!!
        if (Arrays.asList(args).contains("--preload-lib")) {
            System.loadLibrary("forwardsolver"); // for faster first time file opening
            System.loadLibrary("misfit");
            System.out.println("--preload-lib: DLL loaded.");
        }
        // because here is no error handling

        if (OSDetect.isMacOS()) {
            Desktop desktop = Desktop.getDesktop();
            desktop.setOpenFileHandler(e -> {
                e.getFiles()
                        .stream()
                        .map(File::getAbsolutePath)
                        .forEach(macOSHandledFiles::add);
            });
        }
        Application.launch(GemApplication.class, args);
    }
}

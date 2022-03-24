package ru.nucodelabs.gem.app;

import javafx.application.Application;

import java.util.Arrays;

/**
 * Main, запускает приложение JavaFX
 */
public class StartGemApplication {
    public static void main(String[] args) {
        // only for debug!!!
        if (Arrays.asList(args).contains("--preload-lib")) {
            System.loadLibrary("forwardsolver"); // for faster first time file opening
            System.loadLibrary("misfit");
            System.out.println("--preload-lib: DLL loaded.");
        }
        // because here is no error handling

        Application.launch(GemApplication.class, args);
    }
}

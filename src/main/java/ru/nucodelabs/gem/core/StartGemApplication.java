package ru.nucodelabs.gem.core;

import javafx.application.Application;

import java.util.Arrays;

/**
 * Starts the Application
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

        Application.launch(GemApplication.class);
    }
}

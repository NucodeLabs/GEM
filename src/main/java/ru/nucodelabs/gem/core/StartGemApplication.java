package ru.nucodelabs.gem.core;

import javafx.application.Application;

/**
 * Starts the Application
 */
public class StartGemApplication {
    public static void main(String[] args) {
        // only for debug!!!
        System.loadLibrary("forwardsolver"); // for faster first time file opening
        System.loadLibrary("misfit");
        // because here is no error handling

        Application.launch(GemApplication.class);
    }
}

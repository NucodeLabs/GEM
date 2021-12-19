package ru.nucodelabs.gem.core;

import javafx.application.Application;

/**
 * Starts the Application
 */
public class StartGemApplication {
    public static void main(String[] args) {
        System.loadLibrary("forwardsolver"); // for faster first time file opening
        System.loadLibrary("misfit");

        Application.launch(GemApplication.class);
    }
}

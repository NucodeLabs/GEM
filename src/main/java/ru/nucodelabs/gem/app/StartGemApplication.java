package ru.nucodelabs.gem.app;

import javafx.application.Application;

/**
 * Main, запускает приложение JavaFX
 */
public class StartGemApplication {

    public static void main(String[] args) {
        try {
            Application.launch(GemApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

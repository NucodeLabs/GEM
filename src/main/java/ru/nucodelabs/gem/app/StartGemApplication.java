package ru.nucodelabs.gem.app;

import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;

/**
 * Main, запускает приложение JavaFX
 */
@Slf4j
public class StartGemApplication {

    public static void main(String[] args) {
        try {
            Application.launch(GemApplication.class, args);
        } catch (Exception e) {
            log.error("App launch error", e);
        }
    }
}

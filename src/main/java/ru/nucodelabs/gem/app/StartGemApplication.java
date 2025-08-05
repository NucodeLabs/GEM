package ru.nucodelabs.gem.app;

import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main, запускает приложение JavaFX
 */
public class StartGemApplication {

    private static final Logger log = LoggerFactory.getLogger(StartGemApplication.class);

    public static void main(String[] args) {
        try {
            Application.launch(GemApplication.class, args);
        } catch (Exception e) {
            log.error("App launch error", e);
        }
    }
}

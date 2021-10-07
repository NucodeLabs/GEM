package ru.nucodelabs.gem;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("ru/nucodelabs/gem/UI", Locale.ROOT);
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("app-view.fxml"), bundle);

        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

        primaryStage.setTitle("GEM - Main Window");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

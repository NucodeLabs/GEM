package ru.nucodelabs.gem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("ru/nucodelabs/gem/UI", new Locale("ru"));
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("app-view.fxml"), bundle);
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 1280, 720);

        primaryStage.setTitle("GEM - Main Window");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

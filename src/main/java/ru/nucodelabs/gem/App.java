package ru.nucodelabs.gem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    static public Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws IOException {
//        System.out.println(System.getProperty("user.dir"));
        App.primaryStage = primaryStage;
        ResourceBundle bundle = ResourceBundle.getBundle("ru/nucodelabs/gem/UI", new Locale("ru"));
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("app-view.fxml"), bundle);
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 1280, 720);
//        scene.getStylesheets().add(App.class.getResource("style.css").toExternalForm());

        Image icon = new Image("nu.png");
        primaryStage.getIcons().add(icon);

        primaryStage.setTitle("GEM - Main Window");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

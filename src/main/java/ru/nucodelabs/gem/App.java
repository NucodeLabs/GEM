package ru.nucodelabs.gem;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
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

        ResourceBundle bundle = ResourceBundle.getBundle("ru/nucodelabs/gem/UI", new Locale("ru"));     //определяем где лежат необходимы расширения для UI
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("app-view.fxml"), bundle);         //создаем loader для интефейса из fxml файла

        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setTitle("GEM - Main Window");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
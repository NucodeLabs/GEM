package ru.nucodelabs.algorithms.pseudo;

import com.google.inject.Guice;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import ru.nucodelabs.algorithms.interpolation.PseudoInterpolator;
import ru.nucodelabs.gem.app.AppModule;
import ru.nucodelabs.gem.view.color.ColorPalette;

import java.util.ArrayList;
import java.util.List;

public class PseudoTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Drawing Test");
        Group root = new Group();
        double axisX = 800;
        double axisY = 400;
        Canvas canvas = new Canvas(axisX, axisY);

        List<List<XYChart.Data<Double, Double>>> listList = new ArrayList<>();

        listList.add(new ArrayList<>());
        listList.add(new ArrayList<>());
        listList.add(new ArrayList<>());

        var list0 = listList.get(0);
        var list1 = listList.get(1);
        var list2 = listList.get(2);

        list0.add(new XYChart.Data<>(0d, 12d, 44d));
        list0.add(new XYChart.Data<>(0d, 16d, 13d));
        list0.add(new XYChart.Data<>(0d, 20d, 45d));
        list0.add(new XYChart.Data<>(0d, 32d, 33d));
        list0.add(new XYChart.Data<>(0d, 42d, 24d));

        list1.add(new XYChart.Data<>(40d, 12d, 24d));
        list1.add(new XYChart.Data<>(40d, 65d, 31d));
        list1.add(new XYChart.Data<>(40d, 20d, 14d));
        list1.add(new XYChart.Data<>(40d, 32d, 44d));
        list1.add(new XYChart.Data<>(40d, 42d, 24d));

        list2.add(new XYChart.Data<>(100d, 12d, 44d));
        list2.add(new XYChart.Data<>(100d, 16d, 13d));
        list2.add(new XYChart.Data<>(100d, 20d, 45d));
        list2.add(new XYChart.Data<>(100d, 32d, 33d));
        list2.add(new XYChart.Data<>(100d, 42d, 24d));

        var injector = Guice.createInjector(new AppModule());
        PseudoInterpolator interpolator = new PseudoInterpolator(listList, injector.getInstance(ColorPalette.class));
        interpolator.paint(canvas);

        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}

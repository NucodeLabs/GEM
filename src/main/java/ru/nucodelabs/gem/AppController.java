package ru.nucodelabs.gem;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.MODFile;
import ru.nucodelabs.files.sonet.STTFile;
import ru.nucodelabs.files.sonet.Sonet;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AppController {

    @FXML
    public VBox mainPane;
    public MenuItem menuFileOpenEXP;
    public TitledPane vesPane;
    public LineChart<Double, Double> vesCurve;
    public LineChart<Double, Double> inaccuracyCurve;
    public TableView<Double> vesTable;
    public MenuItem menuFileOpenMOD;
    public TitledPane inaccuracyPane;
    public SplitPane vesSplitPane;
    public NumberAxis vesCurveAxisY;
    public NumberAxis vesCurveAxisX;

    @FXML
    public void onMenuFileOpenEXP() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл полевых данных для интерпретации");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("EXP - Полевые данные", "*.EXP", "*.exp")
        );
        File file = chooser.showOpenDialog(mainPane.getScene().getWindow());
//      если закрыть окно выбора файла, ничего не выбрав, то FileChooser вернет null
        if (file == null) {
            return;
        }

        EXPFile openedEXP;
        STTFile openedSTT;
        Path openedFilePath = file.toPath();

        try {
            openedEXP = Sonet.readEXP(file);
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File not found!");
            alert.setContentText(e.getMessage());
            alert.show();
            return;
        }

        try {
            openedSTT = Sonet.readSTT(new File(
                    openedFilePath.getParent().toString()
                            + File.separator
                            + openedEXP.getSTTFileName()));
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("STT file not found!");
            alert.setContentText(e.getMessage());
            alert.show();
            return;
        }
        try {
            App.primaryStage.setTitle(file.getName() + " - GEM");

            vesCurveAxisY.setTickLabelFormatter(powerOf10Formatter);
            vesCurveAxisX.setTickLabelFormatter(powerOf10Formatter);

            ExperimentalCurve.makeCurve(vesCurve, openedSTT, openedEXP);
            InaccuracyCurve.makeCurve(inaccuracyCurve, openedSTT, openedEXP);
        } catch (IndexOutOfBoundsException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot open files");
            alert.setHeaderText("STT and EXP files lines mismatch");
            alert.setContentText(e.getMessage());
            alert.show();
            return;
        }
    }

    @FXML
    public void onMenuFileOpenMOD() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл модели");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("EXP - Данные модели", "*.MOD", "*.mod")
        );
        File file = chooser.showOpenDialog(mainPane.getScene().getWindow());
        if (file == null) {
            return;
        }

        MODFile openedMOD;
        try {
            openedMOD = Sonet.readMOD(file);
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File not found!");
            alert.setContentText(e.getMessage());
            alert.show();
            return;
        }
    }

    protected static StringConverter<Number> powerOf10Formatter = new StringConverter<Number>() {
        Function<String, String> toUpperIndex = string -> {
            ArrayList<Character> resChars = new ArrayList<Character>();
            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                switch (c) {
                    case '1':
                        resChars.add('¹');
                        break;
                    case '2':
                        resChars.add('²');
                        break;
                    case '3':
                        resChars.add('³');
                        break;
                    case '4':
                        resChars.add('⁴');
                        break;
                    case '5':
                        resChars.add('⁵');
                        break;
                    case '6':
                        resChars.add('⁶');
                        break;
                    case '7':
                        resChars.add('⁷');
                        break;
                    case '8':
                        resChars.add('⁸');
                        break;
                    case '9':
                        resChars.add('⁹');
                        break;
                    case '0':
                        resChars.add('⁰');
                        break;
                    case '.':
                        resChars.add('\u0387');
                        break;
                    default:
                        resChars.add(c);
                }
            }
            return resChars
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining());
        };

        @Override
        public String toString(Number object) {
            DecimalFormat format = new DecimalFormat();
            DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
            formatSymbols.setDecimalSeparator(',');
            formatSymbols.setGroupingSeparator('\'');
            return ("10" + toUpperIndex.apply(format.format(object.doubleValue())));
        }

        @Override
        public Number fromString(String string) {
            return null;
        }
    };
}

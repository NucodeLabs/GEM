package ru.nucodelabs.gem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import java.net.URL;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AppController implements Initializable {
    protected static final int EXP_CURVE_SERIES_CNT = 3;
    protected static final int MOD_CURVE_SERIES_CNT = 4;

    EXPFile openedEXP;
    STTFile openedSTT;
    MODFile openedMOD;

    protected static final StringConverter<Number> powerOf10Formatter = new StringConverter<>() {
        final Function<String, String> toUpperIndex = string -> {
            ArrayList<Character> resChars = new ArrayList<>();
            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                switch (c) {
                    case '1' -> resChars.add('¹');
                    case '2' -> resChars.add('²');
                    case '3' -> resChars.add('³');
                    case '4' -> resChars.add('⁴');
                    case '5' -> resChars.add('⁵');
                    case '6' -> resChars.add('⁶');
                    case '7' -> resChars.add('⁷');
                    case '8' -> resChars.add('⁸');
                    case '9' -> resChars.add('⁹');
                    case '0' -> resChars.add('⁰');
                    case '.' -> resChars.add('\u0387');
                    default -> resChars.add(c);
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
            formatSymbols.setDecimalSeparator('.');
            format.setDecimalFormatSymbols(formatSymbols);
            if (object.doubleValue() == 0) {
                return "1";
            } else if (object.doubleValue() - Math.ceil((long) object.doubleValue()) == 0) {
                return ("10" + toUpperIndex.apply(format.format(object.doubleValue())));
            } else {
                return "";
            }
        }

        @Override
        public Number fromString(String string) {
            return null;
        }
    };
    @FXML
    public VBox mainPane;
    public MenuItem menuFileOpenEXP;
    public TitledPane vesPane;

    public LineChart<Double, Double> vesCurve;
    public LineChart<Double, Double> inaccuracyCurve;
    public TableView<TableLine> experimentalTable;
    public TableColumn<TableLine, Double> experimentalAB_2Column;
    public TableColumn<TableLine, Double> experimentalResistanceApparentColumn;
    public TableColumn<TableLine, Double> experimentalErrorResistanceApparentColumn;

    public MenuItem menuFileOpenMOD;
    public TitledPane inaccuracyPane;
    public SplitPane vesSplitPane;
    public NumberAxis vesCurveAxisY;
    public NumberAxis vesCurveAxisX;
    public MenuBar menuBar;

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
            ExperimentalCurve.makeCurve(
                    vesCurve,
                    openedSTT.getAB_2(),
                    openedEXP.getResistanceApparent(),
                    openedEXP.getErrorResistanceApparent());

            ExperimentalTable.makeTable(
                    openedSTT.getAB_2(),
                    openedEXP.getResistanceApparent(),
                    openedEXP.getErrorResistanceApparent(),
                    experimentalTable,
                    experimentalAB_2Column,
                    experimentalResistanceApparentColumn,
                    experimentalErrorResistanceApparentColumn);

        } catch (IndexOutOfBoundsException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot open files");
            alert.setHeaderText("STT and EXP files lines mismatch");
            alert.setContentText(e.getMessage());
            alert.show();
            return;
        }

        App.primaryStage.setTitle(file.getName() + " - GEM");

        vesCurveAxisY.setTickLabelFormatter(powerOf10Formatter);
        vesCurveAxisX.setTickLabelFormatter(powerOf10Formatter);

        if (!vesCurve.isVisible()) {
            vesCurve.setVisible(true);
        }

        if (menuFileOpenMOD.isDisable()) {
            menuFileOpenMOD.setDisable(false);
        }
    }

    @FXML
    public void onMenuFileOpenMOD() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл модели");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MOD - Данные модели", "*.MOD", "*.mod")
        );
        File file = chooser.showOpenDialog(mainPane.getScene().getWindow());
        if (file == null) {
            return;
        }

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

        try {
            TheoreticalCurve.makeCurve(vesCurve, openedSTT.getAB_2(), openedMOD.getResistance(), openedMOD.getPower());
            if (!inaccuracyCurve.isVisible()) {
                inaccuracyCurve.setVisible(true);
            }
            InaccuracyCurve.makeCurve(inaccuracyCurve,
                    openedSTT.getAB_2(),
                    openedEXP.getResistanceApparent(),
                    openedEXP.getErrorResistanceApparent(),
                    TheoreticalCurve.getSolvedResistance());
        } catch (UnsatisfiedLinkError e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Невозможно решить прямую задачу");
            alert.setHeaderText("Отсутствует библиотека ForwardSolver");
            alert.setContentText(e.getMessage());
            alert.show();
            return;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("mac")) {
            menuBar.setUseSystemMenuBar(true);
        }
    }
}

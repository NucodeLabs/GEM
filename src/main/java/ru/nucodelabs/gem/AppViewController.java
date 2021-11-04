package ru.nucodelabs.gem;

import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.STTFile;
import ru.nucodelabs.files.sonet.Sonet;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AppViewController {

    @FXML
    public VBox mainPane;
    public MenuItem menuFileOpenEXP;
    public TitledPane vesPane;

    @FXML
    public LineChart vesCurve;

    @FXML
    public TableView vesTable;

    @FXML
    public void onMenuFileOpenEXP() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose files for interpretation");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("EXP - Полевые данные", "*.EXP", "*.exp")
        );
        File file = chooser.showOpenDialog(mainPane.getScene().getWindow());
        EXPFile openedEXP = new EXPFile();
        STTFile openedSTT = new STTFile();
        Path openedFilePath = file.toPath();

        try {
            openedEXP = Sonet.readEXP(file);

        } catch (FileNotFoundException e) {
            System.out.println("Error: EXP file not found!");
            System.out.println(e);
            e.printStackTrace();
        }

        try {
            openedSTT = Sonet.readSTT(new File(
                    openedFilePath.getParent().toString()
                            + File.separator
                            + openedEXP.getSTTFileName()));
        } catch (FileNotFoundException e) {
            System.out.println("Error: No .STT file not found!");
            System.out.println("Put corresponding .STT file in same directory.");
            System.out.println(e);
            e.printStackTrace();
        }

        //fillTable(openedSTT, openedEXP);
        makeCurve(openedSTT, openedEXP);


    }

    public void makeCurve(STTFile openedSTT, EXPFile openedEXP) {
        vesCurve.getData().clear();
        vesCurve.getData().addAll(makeCurveData(openedSTT, openedEXP));
    }

    private XYChart.Series makeCurveData(STTFile openedSTT, EXPFile openedEXP) {
        XYChart.Series pointsSeries = new XYChart.Series<>();
        ArrayList<Double> arrayAB_2 = openedSTT.getAB_2();
        ArrayList<Double> arrayRes = openedEXP.getResistanceApp();

        for (int i = 0; i < arrayAB_2.size(); i++) {
            pointsSeries.getData().add(new XYChart.Data<>(Math.log(arrayAB_2.get(i)), Math.log(arrayRes.get(i))));
        }

        return pointsSeries;
    }

   /* public void fillTable(STTFile openedSTT, EXPFile openedEXP) {
        int columnCnt = openedEXP.getColumnCnt();
        ArrayList<Double> arrayAB_2 = openedSTT.getAB_2();
        ArrayList<Double> arrayRes = openedEXP.getResistanceApp();
        ArrayList<Double> arrayAmp = openedEXP.getAmperage();
        ArrayList<Double> arrayVolt = openedEXP.getVoltage();
        ArrayList<Double> arrayPolar = openedEXP.getPolarizationApp();

        vesTable.getItems().clear();
        ArrayList<TablePair> allPairs = new ArrayList<>();
        for (int i = 0; i < arrayAB_2.size(); i++) {
            allPairs.add(new TablePair(arrayAB_2.get(i), arrayRes.get(i)));
        }
        ObservableList<TablePair> obList = FXCollections.observableArrayList(allPairs);
        vesTable.setItems(obList);

        TableColumn<Double,Double> colAB_2 = new TableColumn<>("AB/2");
        colAB_2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Double, Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<Double, Double> param) {
                return param.getValue(;
            }
        });
        TableColumn<Person,String> lastNameCol = new TableColumn<Person,String>("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory("lastName"));

        table.getColumns().setAll(firstNameCol, lastNameCol);
    }*/

}

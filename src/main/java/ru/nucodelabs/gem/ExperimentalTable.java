package ru.nucodelabs.gem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.nucodelabs.files.sonet.TableLine;

import java.util.ArrayList;
import java.util.List;

public class ExperimentalTable {

    protected static void makeTable(
            List<Double> AB_2,
            List<Double> resistanceApp,
            List<Double> errorResistanceApp,
            TableView<TableLine> experimentalTable,
            TableColumn<TableLine, Double> experimentalAB_2Column,
            TableColumn<TableLine, Double> experimentalResistanceAppColumn,
            TableColumn<TableLine, Double> experimentalErrorResistanceAppColumn) {

        experimentalAB_2Column.setCellValueFactory(new PropertyValueFactory<>("AB_2"));

        experimentalResistanceAppColumn.setCellValueFactory(new PropertyValueFactory<>("resistanceApp"));

        experimentalErrorResistanceAppColumn.setCellValueFactory(new PropertyValueFactory<>("errorResistanceApp"));

        ObservableList<TableLine> tableContent = makeTableContent(AB_2, resistanceApp, errorResistanceApp);
        experimentalTable.setItems(tableContent);
    }

    protected static ObservableList<TableLine> makeTableContent(List<Double> AB_2,
                                                                List<Double> resistanceApp,
                                                                List<Double> errorResistanceApp) {
        List<TableLine> tempList = new ArrayList<>();
        for (int i = 0; i < AB_2.size(); i++) {
            TableLine tableLine = new TableLine();

            tableLine.setAB_2(AB_2.get(i));
            tableLine.setResistanceApp(resistanceApp.get(i));
            tableLine.setErrorResistanceApp(errorResistanceApp.get(i));

            tempList.add(tableLine);
        }

        return FXCollections.observableArrayList(tempList);
    }
}
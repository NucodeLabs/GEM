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
            List<Double> listRangeAB_2,
            List<Double> listResistance,
            List<Double> listResistanceError,
            TableView<TableLine> experimentalTable,
            TableColumn<TableLine, Double> experimentalRangeAB_2Column,
            TableColumn<TableLine, Double> experimentalResistanceColumn,
            TableColumn<TableLine, Double> experimentalResistanceErrorColumn) {

        experimentalRangeAB_2Column.setCellValueFactory(new PropertyValueFactory<>("rangeAB"));

        experimentalResistanceColumn.setCellValueFactory(new PropertyValueFactory<>("resistivity"));

        experimentalResistanceErrorColumn.setCellValueFactory(new PropertyValueFactory<>("resistivityError"));

        ObservableList<TableLine> tableContent = makeTableContent(listRangeAB_2, listResistance, listResistanceError);
        experimentalTable.setItems(tableContent);
    }

    protected static ObservableList<TableLine> makeTableContent(List<Double> listAB_2,
                                                                List<Double> listResistance,
                                                                List<Double> listResistanceError) {
        List<TableLine> tempList = new ArrayList<>();
        for (int i = 0; i < listAB_2.size(); i++) {
            TableLine tableLine = new TableLine();

            tableLine.setRangeAB(listAB_2.get(i));
            tableLine.setResistivity(listResistance.get(i));
            tableLine.setResistivityError(listResistanceError.get(i));

            tempList.add(tableLine);
        }

        return FXCollections.observableArrayList(tempList);
    }
}
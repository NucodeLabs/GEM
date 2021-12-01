package ru.nucodelabs.gem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class ExperimentalTable {

    protected static void makeTable(
            List<Double> AB_2,
            List<Double> resistanceApparent,
            List<Double> errorResistanceApparent,
            TableView<TableLine> experimentalTable,
            TableColumn<TableLine, Double> experimentalAB_2Column,
            TableColumn<TableLine, Double> experimentalResistanceApparentColumn,
            TableColumn<TableLine, Double> experimentalErrorResistanceApparentColumn) {

        experimentalAB_2Column.setCellValueFactory(new PropertyValueFactory<>("AB_2"));

        experimentalResistanceApparentColumn.setCellValueFactory(new PropertyValueFactory<>("resistanceApparent"));

        experimentalErrorResistanceApparentColumn.setCellValueFactory(new PropertyValueFactory<>("errorResistanceApparent"));

        ObservableList<TableLine> tableContent = makeTableContent(AB_2, resistanceApparent, errorResistanceApparent);
        experimentalTable.setItems(tableContent);
    }

    protected static ObservableList<TableLine> makeTableContent(List<Double> AB_2,
                                                                List<Double> resistanceApparent,
                                                                List<Double> errorResistanceApparent) {
        List<TableLine> tableContent = new ArrayList<>();
        for (int i = 0; i < AB_2.size(); i++) {
            TableLine tableLine = new TableLine();

            tableLine.setAB_2(AB_2.get(i));
            tableLine.setResistanceApparent(resistanceApparent.get(i));
            tableLine.setErrorResistanceApp(errorResistanceApparent.get(i));

            tableContent.add(tableLine);
        }

        return FXCollections.observableArrayList(tableContent);
    }
}
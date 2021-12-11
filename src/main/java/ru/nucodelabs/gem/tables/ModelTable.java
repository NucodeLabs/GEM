package ru.nucodelabs.gem.tables;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.nucodelabs.data.ModelData;

import java.util.ArrayList;
import java.util.List;

public class ModelTable {

    protected static void makeTable( // ИСПРАВИТЬ СИГНАТУРУ НА ИСПОЛЬЗОВАНИЕ ExperimentalData И ModelData
                                     TableView<TableLine> modelTable,
                                     TableColumn<TableLine, Double> modelPowerColumn,
                                     TableColumn<TableLine, Double> modelResistanceAppColumn,
                                     TableColumn<TableLine, Double> modelPolarizationAppColumn,
                                     ModelData modelData) {

        modelPowerColumn.setCellValueFactory(new PropertyValueFactory<>("power"));
        modelResistanceAppColumn.setCellValueFactory(new PropertyValueFactory<>("resistanceApparent"));
        modelPolarizationAppColumn.setCellValueFactory(new PropertyValueFactory<>("polarizationApparent"));

        ObservableList<TableLine> tableContent = makeTableContent(
                modelData.getResistance(),
                modelData.getPolarization(),
                modelData.getPower());

        modelTable.setItems(tableContent);
    }

    public static void initializeWithData(
            TableView<TableLine> modelTable,
            TableColumn<TableLine, Double> modelPowerColumn,
            TableColumn<TableLine, Double> modelResistanceApparentColumn,
            TableColumn<TableLine, Double> modelPolarizationApparentColumn,
            ModelData modelData
    ) {
        makeTable(
                modelTable,
                modelPowerColumn,
                modelResistanceApparentColumn,
                modelPolarizationApparentColumn,
                modelData
        );
    }

    protected static ObservableList<TableLine> makeTableContent(List<Double> listResistanceApparent,
                                                                List<Double> listPolarization,
                                                                List<Double> listPower) {
        List<TableLine> tempList = new ArrayList<>();
        for (int i = 0; i < listPower.size(); i++) {
            TableLine tableLine = new TableLine();

            tableLine.setResistanceApparent(listResistanceApparent.get(i));
            tableLine.setPower(listPower.get(i));
            //tableLine.setPolarizationApparent(listPolarization.get(i));

            tempList.add(tableLine);
        }

        return FXCollections.observableArrayList(tempList);
    }
}
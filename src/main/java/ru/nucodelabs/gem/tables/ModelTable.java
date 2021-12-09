package ru.nucodelabs.gem.tables;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class ModelTable {

    protected static void makeTable( // ИСПРАВИТЬ СИГНАТУРУ НА ИСПОЛЬЗОВАНИЕ ExperimentalData И ModelData
                                     List<Double> listRangeAB_2,
                                     List<Double> listRangeMN_2,
                                     List<Double> listAmperage,
                                     List<Double> listVoltage,
                                     List<Double> listResistance,
                                     List<Double> listResistanceError,
                                     List<Double> listPolarization,
                                     List<Double> listPolarizationError,
                                     TableView<TableLine> experimentalTable,
                                     TableColumn<TableLine, Double> experimentalAB_2Column,
                                     TableColumn<TableLine, Double> experimentalMN_2Column,
                                     TableColumn<TableLine, Double> experimentalAmperageColumn,
                                     TableColumn<TableLine, Double> experimentalVoltageColumn,
                                     TableColumn<TableLine, Double> experimentalResistanceAppColumn,
                                     TableColumn<TableLine, Double> experimentalErrorResistanceAppColumn,
                                     TableColumn<TableLine, Double> experimentalPolarizationAppColumn,
                                     TableColumn<TableLine, Double> experimentalErrorPolarizationAppColumn) {

        experimentalAB_2Column.setCellValueFactory(new PropertyValueFactory<>("rangeAB"));
        experimentalMN_2Column.setCellValueFactory(new PropertyValueFactory<>("rangeMN"));
        experimentalAmperageColumn.setCellValueFactory(new PropertyValueFactory<>("amperage"));
        experimentalVoltageColumn.setCellValueFactory(new PropertyValueFactory<>("voltage"));
        experimentalResistanceAppColumn.setCellValueFactory(new PropertyValueFactory<>("resistivity"));
        experimentalErrorResistanceAppColumn.setCellValueFactory(new PropertyValueFactory<>("resistivityError"));
        experimentalPolarizationAppColumn.setCellValueFactory(new PropertyValueFactory<>("polarization"));
        experimentalErrorPolarizationAppColumn.setCellValueFactory(new PropertyValueFactory<>("polarizationError"));

        ObservableList<TableLine> tableContent = makeTableContent(
                listRangeAB_2,
                listRangeMN_2,
                listAmperage,
                listVoltage,
                listResistance,
                listResistanceError,
                listPolarization,
                listPolarizationError);

        experimentalTable.setItems(tableContent);
    }

    protected static ObservableList<TableLine> makeTableContent(List<Double> listAB_2,
                                                                List<Double> listMN_2,
                                                                List<Double> listAmperage,
                                                                List<Double> listVoltage,
                                                                List<Double> listResistance,
                                                                List<Double> listResistanceError,
                                                                List<Double> listPolarization,
                                                                List<Double> listPolarizationError) {
        List<TableLine> tempList = new ArrayList<>();
        for (int i = 0; i < listAB_2.size(); i++) {
            TableLine tableLine = new TableLine();

            tableLine.setAB_2(listAB_2.get(i));
            tableLine.setMN_2(listMN_2.get(i));
            tableLine.setAB_2(listAmperage.get(i));
            tableLine.setAB_2(listVoltage.get(i));
            tableLine.setResistanceApparent(listResistance.get(i));
            tableLine.setErrorResistanceApp(listResistanceError.get(i));
            tableLine.setPolarizationApp(listPolarization.get(i));
            tableLine.setErrorPolarizationApp(listPolarizationError.get(i));

            tempList.add(tableLine);
        }

        return FXCollections.observableArrayList(tempList);
    }
}
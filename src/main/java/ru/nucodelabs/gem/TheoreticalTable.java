package ru.nucodelabs.gem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.STTFile;
import ru.nucodelabs.files.sonet.TableLine;

import java.util.ArrayList;
import java.util.List;

public class TheoreticalTable {

    protected static void makeTable(
            EXPFile openedEXP,
            STTFile openedSTT,
            TableView<TableLine> experimentalTable,
            TableColumn<TableLine, Double> experimentalRangeAB_2Column,
            TableColumn<TableLine, Double> experimentalRangeMN_2Column,
            TableColumn<TableLine, Double> experimentalAmperageColumn,
            TableColumn<TableLine, Double> experimentalVoltageColumn,
            TableColumn<TableLine, Double> experimentalResistanceColumn,
            TableColumn<TableLine, Double> experimentalResistanceErrorColumn,
            TableColumn<TableLine, Double> experimentalPolarizationColumn,
            TableColumn<TableLine, Double> experimentalPolarizationErrorColumn) {

        experimentalRangeAB_2Column.setCellValueFactory(new PropertyValueFactory<>("rangeAB"));
        experimentalRangeMN_2Column.setCellValueFactory(new PropertyValueFactory<>("rangeMN"));
        experimentalAmperageColumn.setCellValueFactory(new PropertyValueFactory<>("amperage"));
        experimentalVoltageColumn.setCellValueFactory(new PropertyValueFactory<>("voltage"));
        experimentalResistanceColumn.setCellValueFactory(new PropertyValueFactory<>("resistivity"));
        experimentalResistanceErrorColumn.setCellValueFactory(new PropertyValueFactory<>("resistivityError"));
        experimentalPolarizationColumn.setCellValueFactory(new PropertyValueFactory<>("polarization"));
        experimentalPolarizationErrorColumn.setCellValueFactory(new PropertyValueFactory<>("polarizationError"));

        List<Double> listRangeAB_2 = openedSTT.getAB_2();
        List<Double> listRangeMN_2 = openedSTT.getMN_2();
        List<Double> listAmperage = openedEXP.getAmperage();
        List<Double> listVoltage = openedEXP.getVoltage();
        List<Double> listResistance = openedEXP.getResistanceApp();
        List<Double> listResistanceError = openedEXP.getErrorResistanceApp();
        List<Double> listPolarization = openedEXP.getPolarizationApp();
        List<Double> listPolarizationError = openedEXP.getErrorPolarizationApp();

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

            tableLine.setRangeAB(listAB_2.get(i));
            tableLine.setRangeMN(listMN_2.get(i));
            tableLine.setRangeAB(listAmperage.get(i));
            tableLine.setRangeAB(listVoltage.get(i));
            tableLine.setResistivity(listResistance.get(i));
            tableLine.setResistivityError(listResistanceError.get(i));
            tableLine.setPolarization(listPolarization.get(i));
            tableLine.setPolarizationError(listPolarizationError.get(i));

            tempList.add(tableLine);
        }

        return FXCollections.observableArrayList(tempList);
    }
}
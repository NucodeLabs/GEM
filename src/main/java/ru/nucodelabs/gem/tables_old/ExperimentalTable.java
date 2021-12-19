package ru.nucodelabs.gem.tables_old;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.nucodelabs.data.ves.ExperimentalData;

import java.util.ArrayList;
import java.util.List;

public class ExperimentalTable {

    protected static void makeTable(
            TableView<TableLine> experimentalTable,
            TableColumn<TableLine, Double> experimentalAB_2Column,
            TableColumn<TableLine, Double> experimentalMN_2Column,
            TableColumn<TableLine, Double> experimentalResistanceApparentColumn,
            TableColumn<TableLine, Double> experimentalAmperageColumn,
            TableColumn<TableLine, Double> experimentalVoltageColumn,
            TableColumn<TableLine, Double> experimentalErrorResistanceApparentColumn,
            ExperimentalData experimentalData
    ) {

        experimentalAB_2Column.setCellValueFactory(new PropertyValueFactory<>("AB_2"));

        experimentalMN_2Column.setCellValueFactory(new PropertyValueFactory<>("MN_2"));

        experimentalResistanceApparentColumn.setCellValueFactory(new PropertyValueFactory<>("resistanceApparent"));

        experimentalAmperageColumn.setCellValueFactory(new PropertyValueFactory<>("amperage"));

        experimentalVoltageColumn.setCellValueFactory(new PropertyValueFactory<>("voltage"));

        experimentalErrorResistanceApparentColumn.setCellValueFactory(new PropertyValueFactory<>("errorResistanceApparent"));

        ObservableList<TableLine> tableContent = makeTableContent(experimentalData);
        experimentalTable.setItems(tableContent);
    }

    public static void initializeWithData(
            TableView<TableLine> experimentalTable,
            TableColumn<TableLine, Double> experimentalAB_2Column,
            TableColumn<TableLine, Double> experimentalMN_2Column,
            TableColumn<TableLine, Double> experimentalResistanceApparentColumn,
            TableColumn<TableLine, Double> experimentalAmperageColumn,
            TableColumn<TableLine, Double> experimentalVoltageColumn,
            TableColumn<TableLine, Double> experimentalErrorResistanceApparentColumn,
            ExperimentalData experimentalData
    ) {
        makeTable(
                experimentalTable,
                experimentalAB_2Column,
                experimentalMN_2Column,
                experimentalResistanceApparentColumn,
                experimentalAmperageColumn,
                experimentalVoltageColumn,
                experimentalErrorResistanceApparentColumn,
                experimentalData
        );
    }

    protected static ObservableList<TableLine> makeTableContent(ExperimentalData experimentalData) {
        final ArrayList<Double> AB_2 = new ArrayList<>(experimentalData.getAB_2());
        final ArrayList<Double> MN_2 = new ArrayList<>(experimentalData.getMN_2());
        final ArrayList<Double> resistanceApparent = new ArrayList<>(experimentalData.getResistanceApparent());
        final ArrayList<Double> amperage = new ArrayList<>(experimentalData.getAmperage());
        final ArrayList<Double> voltage = new ArrayList<>(experimentalData.getVoltage());
        final ArrayList<Double> errorResistanceApparent = new ArrayList<>(experimentalData.getErrorResistanceApparent());

        List<TableLine> tableContent = new ArrayList<>();
        for (int i = 0; i < experimentalData.getSize(); i++) {
            TableLine tableLine = new TableLine();

            tableLine.setAB_2(AB_2.get(i));
            tableLine.setMN_2(MN_2.get(i));
            tableLine.setResistanceApparent(resistanceApparent.get(i));
            tableLine.setAmperage(amperage.get(i));
            tableLine.setVoltage(voltage.get(i));
            tableLine.setErrorResistanceApp(errorResistanceApparent.get(i));

            tableContent.add(tableLine);
        }

        return FXCollections.observableArrayList(tableContent);
    }
}
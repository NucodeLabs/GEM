package ru.nucodelabs.files.sonet;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

final class SonetImportUtils {
    private SonetImportUtils() {
    } // чтобы нельзя было создать экземпляр класса Sonet

    static STTFile readSTT(File file) throws Exception {
        STTFile res = new STTFile();
        Scanner sc = new Scanner(file).useLocale(Locale.US);

        ArrayList<ArrayList<Double>> numbers = columnReader(sc, new STTFile().getColumnCnt());

        res.getAB_2().addAll(
                numbers.stream().map(s -> s.get(0)).toList());
        res.getMN_2().addAll(
                numbers.stream().map(s -> s.get(1)).toList());

        sc.close();
        res.setFile(file);
        return res;
    }

    static EXPFile readEXP(File file) throws Exception {
        EXPFile res = new EXPFile();
        Scanner sc = new Scanner(file, "Cp866").useLocale(Locale.US);
        res.setSTTFileName(sc.nextLine());
        readPassport(sc, res);
        ArrayList<ArrayList<Double>> numbers = null;
        if (sc.hasNext("\\$")) {
            sc.next();
            numbers = columnReader(sc, new EXPFile().getColumnCnt());
        }
        assert numbers != null;

        res.getAmperage().addAll(
                numbers.stream().map(s -> s.get(0)).toList());
        res.getVoltage().addAll(
                numbers.stream().map(s -> s.get(1)).toList());
        res.getResistanceApparent().addAll(
                numbers.stream().map(s -> s.get(2)).toList());
        res.getErrorResistanceApparent().addAll(
                numbers.stream().map(s -> s.get(3)).toList());
        res.getPolarizationApparent().addAll(
                numbers.stream().map(s -> s.get(4)).toList());
        res.getErrorPolarizationApparent().addAll(
                numbers.stream().map(s -> s.get(5)).toList());

        sc.close();
        res.setFile(file);
        return res;
    }

    static MODFile readMOD(File file) throws Exception {
        MODFile res = new MODFile();
        Scanner sc = new Scanner(file).useLocale(Locale.US);

        ArrayList<ArrayList<Double>> numbers = columnReader(sc, new MODFile().getColumnCnt());

        res.getResistance().addAll(
                numbers.stream().map(s -> s.get(0)).toList());
        res.getPower().addAll(
                numbers.stream().map(s -> s.get(1)).toList());
        res.getPolarization().addAll(
                numbers.stream().map(s -> s.get(2)).toList());

        sc.close();
        res.setFile(file);
        return res;
    }

    private static void readPassport(Scanner sc, EXPFile res) {
        ArrayList<String> strList = new ArrayList<>();
        while (sc.hasNextLine() && !sc.hasNext("\\$") && strList.size() < 6) {
            strList.add(sc.nextLine());
        }
        if (strList.size() > 0) res.setNumber(strList.get(0));
        if (strList.size() > 1) res.setDate(strList.get(1));
        if (strList.size() > 2) res.setWeather(strList.get(2));
        if (strList.size() > 3) res.setOperator(strList.get(3));
        if (strList.size() > 4) res.setInterpreter(strList.get(4));
        if (strList.size() > 5) res.setChecked(strList.get(5));
    }

    private static ArrayList<ArrayList<Double>> columnReader(Scanner sc, int colCnt) {
        ArrayList<ArrayList<Double>> res = new ArrayList<>();
        while (sc.hasNextLine() && !sc.hasNext("\\$") && !sc.hasNext("-1.0")) {
            String str = sc.nextLine();
            if (str.isBlank()) {
                continue;
            }
            Scanner strSc = new Scanner(str).useLocale(Locale.US);
            ArrayList<Double> numList = new ArrayList<>();
            for (int i = 0; i < colCnt; i++) {
                if (strSc.hasNext()) {
                    numList.add(strSc.nextDouble());
                } else {
                    numList.add(0d);
                }
            }
            res.add(numList);
            strSc.close();
        }
        return res;
    }
}

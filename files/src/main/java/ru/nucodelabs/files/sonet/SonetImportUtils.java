package ru.nucodelabs.files.sonet;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

final class SonetImportUtils {
    private SonetImportUtils() {
    }

    static @NotNull STTFile readSTT(@NotNull File file) throws FileNotFoundException {
        STTFile res = new STTFile();
        try (Scanner sc = new Scanner(file).useLocale(Locale.US)) {
            ArrayList<ArrayList<Double>> numbers = columnReader(sc, new STTFile().getColumnCnt());

            res.getAB_2().addAll(
                numbers.stream().map(s -> s.get(0)).toList());
            res.getMN_2().addAll(
                numbers.stream().map(s -> s.get(1)).toList());
        }
        res.setFile(file);
        return res;
    }

    static @NotNull EXPFile readEXP(@NotNull File file) throws FileNotFoundException {
        EXPFile res = new EXPFile();
        try (Scanner sc = new Scanner(file, "Cp866").useLocale(Locale.US)) {
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
            res.getResistivityApparent().addAll(
                numbers.stream().map(s -> s.get(2)).toList());
            res.getErrorResistivityApparent().addAll(
                numbers.stream().map(s -> s.get(3)).toList());
            res.getPolarizationApparent().addAll(
                numbers.stream().map(s -> s.get(4)).toList());
            res.getErrorPolarizationApparent().addAll(
                numbers.stream().map(s -> s.get(5)).toList());
        }
        res.setFile(file);
        return res;
    }

    static @NotNull MODFile readMOD(@NotNull File file) throws FileNotFoundException {
        MODFile res = new MODFile();
        try (Scanner sc = new Scanner(file).useLocale(Locale.US)) {
            ArrayList<ArrayList<Double>> numbers = columnReader(sc, new MODFile().getColumnCnt());

            res.getResistivity().addAll(
                numbers.stream().map(s -> s.get(0)).toList());
            res.getPower().addAll(
                numbers.stream().map(s -> s.get(1)).toList());
            res.getPolarization().addAll(
                numbers.stream().map(s -> s.get(2)).toList());
        }
        res.setFile(file);
        return res;
    }

    private static void readPassport(@NotNull Scanner sc, @NotNull EXPFile res) {
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

    private static @NotNull ArrayList<ArrayList<Double>> columnReader(@NotNull Scanner sc, int colCnt) {
        ArrayList<ArrayList<Double>> res = new ArrayList<>();
        while (sc.hasNextLine() && !sc.hasNext("\\$") && !sc.hasNext("-1.0")) {
            String str = sc.nextLine();
            if (str.isBlank()) {
                continue;
            }
            try (Scanner strSc = new Scanner(str).useLocale(Locale.US)) {
                ArrayList<Double> numList = new ArrayList<>();
                for (int i = 0; i < colCnt; i++) {
                    if (strSc.hasNext()) {
                        numList.add(strSc.nextDouble());
                    } else {
                        numList.add(0d);
                    }
                }
                res.add(numList);
            }
        }
        return res;
    }
}

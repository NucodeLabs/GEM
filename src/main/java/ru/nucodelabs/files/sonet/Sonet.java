package ru.nucodelabs.files.sonet;

import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.MODFile;
import ru.nucodelabs.files.sonet.STTFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class Sonet {
    private Sonet() {
    } // чтобы нельзя было создать экземпляр класса Sonet

    public static STTFile readSTT(File file) throws FileNotFoundException {
        STTFile res = new STTFile();
        Scanner sc = new Scanner(file).useLocale(Locale.US);
        ArrayList<ArrayList<Double>> arrList;
        arrList = columnParser(sc);
        for (ArrayList<Double> numList : arrList) {
            if (numList.size() > 0) res.getAB_2().add(numList.get(0));
            if (numList.size() > 1) res.getMN_2().add(numList.get(1));
        }
        sc.close();
        return res;
    }

    public static EXPFile readEXP(File file) throws FileNotFoundException {
        EXPFile res = new EXPFile();
        Scanner sc = new Scanner(file).useLocale(Locale.US);
        res.setSTTFileName(sc.nextLine());
        readPassport(sc, res);
        ArrayList<ArrayList<Double>> arrList = null;
        if (sc.hasNext("\\$")) {
            sc.next();
            arrList = columnParser(sc);
        }
        assert arrList != null; // Чтоб компилятор не агрился
        for (ArrayList<Double> numList : arrList) {
            if (numList.size() > 0) res.getAmperage().add(numList.get(0));
            if (numList.size() > 1) res.getVoltage().add(numList.get(1));
            if (numList.size() > 2) res.getResistanceApp().add(numList.get(2));
            if (numList.size() > 3) res.getErrorResistanceApp().add(numList.get(3));
            if (numList.size() > 4) res.getPolarizationApp().add(numList.get(4));
            if (numList.size() > 5) res.getErrorPolarizationApp().add(numList.get(5));
        }
        sc.close();
        return res;
    }

    public static MODFile readMOD(File file) throws FileNotFoundException {
        MODFile res = new MODFile();
        Scanner sc = new Scanner(file).useLocale(Locale.US);
        ArrayList<ArrayList<Double>> arrList;
        arrList = columnParser(sc);
        for (ArrayList<Double> numList : arrList) {
            if (numList.size() > 0) res.getResistance().add(numList.get(0));
            if (numList.size() > 1) res.getPower().add(numList.get(1));
            if (numList.size() > 1) res.getPolarization().add(numList.get(1));
        }
        sc.close();
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

    private static ArrayList<ArrayList<Double>> columnParser(Scanner sc) {
        ArrayList<ArrayList<Double>> arrList = new ArrayList<>();
        while (sc.hasNextLine() && !sc.hasNext("\\$") && !sc.hasNext("-1.0")) {
            String str = sc.nextLine();
            String[] strList = str.split("\s+");
            ArrayList<Double> numList = new ArrayList<>();
            for (String st : strList) {
                if (!st.isEmpty()) {
                    numList.add(Double.parseDouble(st));
                }
            }
            arrList.add(numList);
        }
        sc.nextLine();
        return arrList;
    }
}

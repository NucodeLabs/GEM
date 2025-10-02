package ru.nucodelabs.files.sonet;

import java.io.File;
import java.util.ArrayList;

public class EXPFile {
    private String STTFileName;
    private File file;

    private String number; // Номер установки
    private String date; // Дата
    private String weather; // Погода
    private String operator; // Оператор
    private String interpreter; // Интерпретатор
    private String checked; // Проверил

    private ArrayList<Double> amperage; // Ток, мА
    private ArrayList<Double> voltage; // Напряжение, мВ
    private ArrayList<Double> resistivityApparent; // Сопротивление кажущееся, Ом * м
    private ArrayList<Double> errorResistivityApparent; // Погрешность, %
    private ArrayList<Double> polarizationApparent; // Поляризация кажущаяся, %
    private ArrayList<Double> errorPolarizationApparent; // Погрешность, %

    public EXPFile() {
        STTFileName = "";
        interpreter = "";
        number = "";
        date = "";
        weather = "";
        operator = "";
        checked = "";
        amperage = new ArrayList<>();
        voltage = new ArrayList<>();
        resistivityApparent = new ArrayList<>();
        errorResistivityApparent = new ArrayList<>();
        polarizationApparent = new ArrayList<>();
        errorPolarizationApparent = new ArrayList<>();
    }

    public int getColumnCnt() {
        return 6;
    }

    public String getNumber() {
        return number;
    }

    public String getDate() {
        return date;
    }

    public String getWeather() {
        return weather;
    }

    public String getOperator() {
        return operator;
    }

    public String getInterpreter() {
        return interpreter;
    }

    public String getChecked() {
        return checked;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public void setInterpreter(String interpreter) {
        this.interpreter = interpreter;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }


    public ArrayList<Double> getAmperage() {
        return amperage;
    }

    public ArrayList<Double> getVoltage() {
        return voltage;
    }

    public ArrayList<Double> getResistivityApparent() {
        return resistivityApparent;
    }

    public ArrayList<Double> getErrorResistivityApparent() {
        return errorResistivityApparent;
    }

    public ArrayList<Double> getPolarizationApparent() {
        return polarizationApparent;
    }

    public ArrayList<Double> getErrorPolarizationApparent() {
        return errorPolarizationApparent;
    }

    public String getSTTFileName() {
        return STTFileName;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setAmperage(ArrayList<Double> amperage) {
        this.amperage = amperage;
    }

    public void setVoltage(ArrayList<Double> voltage) {
        this.voltage = voltage;
    }

    public void setResistivityApparent(ArrayList<Double> resistivityApparent) {
        this.resistivityApparent = resistivityApparent;
    }

    public void setErrorResistivityApparent(ArrayList<Double> errorResistivityApparent) {
        this.errorResistivityApparent = errorResistivityApparent;
    }

    public void setPolarizationApparent(ArrayList<Double> polarizationApparent) {
        this.polarizationApparent = polarizationApparent;
    }

    public void setErrorPolarizationApparent(ArrayList<Double> errorPolarizationApparent) {
        this.errorPolarizationApparent = errorPolarizationApparent;
    }

    public void setSTTFileName(String STTFileName) {
        this.STTFileName = STTFileName;
    }
}

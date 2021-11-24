package ru.nucodelabs.files.sonet;

import java.util.ArrayList;

public class EXPFile {
    public EXPFile() {
    }

    private String STTFileName = "";

    private String number = ""; // Номер установки
    private String date = ""; // Дата
    private String weather = ""; // Погода
    private String operator = ""; // Оператор
    private String interpreter = ""; // Интерпретатор
    private String checked = ""; // Проверил

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


    private ArrayList<Double> amperage = new ArrayList<>(0); // Ток, мА
    private ArrayList<Double> voltage = new ArrayList<>(0); // Напряжение, мВ
    private ArrayList<Double> resistanceApparent = new ArrayList<>(0); // Сопротивление кажущееся, Ом * м
    private ArrayList<Double> errorResistanceApparent = new ArrayList<>(0); // Погрешность, %
    private ArrayList<Double> polarizationApparent = new ArrayList<>(0); // Поляризация кажущаяся, %
    private ArrayList<Double> errorPolarizationApparent = new ArrayList<>(0); // Погрешность, %

    public int getColumnCnt() {
        return 6;
    }

    public ArrayList<Double> getAmperage() {
        return amperage;
    }

    public ArrayList<Double> getVoltage() {
        return voltage;
    }

    public ArrayList<Double> getResistanceApparent() {
        return resistanceApparent;
    }

    public ArrayList<Double> getErrorResistanceApparent() {
        return errorResistanceApparent;
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

    public void setAmperage(ArrayList<Double> amperage) {
        this.amperage = amperage;
    }

    public void setVoltage(ArrayList<Double> voltage) {
        this.voltage = voltage;
    }

    public void setResistanceApparent(ArrayList<Double> resistanceApparent) {
        this.resistanceApparent = resistanceApparent;
    }

    public void setErrorResistanceApparent(ArrayList<Double> errorResistanceApparent) {
        this.errorResistanceApparent = errorResistanceApparent;
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

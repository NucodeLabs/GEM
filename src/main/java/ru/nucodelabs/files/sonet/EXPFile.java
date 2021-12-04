package ru.nucodelabs.files.sonet;

import java.util.ArrayList;

public class EXPFile {
    private String STTFileName;

    private String number; // Номер установки
    private String date; // Дата
    private String weather; // Погода
    private String operator; // Оператор
    private String interpreter; // Интерпретатор
    private String checked; // Проверил

    private ArrayList<Double> amperage; // Ток, мА
    private ArrayList<Double> voltage; // Напряжение, мВ
    private ArrayList<Double> resistanceApparent; // Сопротивление кажущееся, Ом * м
    private ArrayList<Double> errorResistanceApparent; // Погрешность, %
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
        resistanceApparent = new ArrayList<>();
        errorResistanceApparent = new ArrayList<>();
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

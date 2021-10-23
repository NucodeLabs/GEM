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
    private ArrayList<Double> resistanceApp = new ArrayList<>(0); // Сопротивление кажущееся, Ом * м
    private ArrayList<Double> errorResistanceApp = new ArrayList<>(0); // Погрешность, %
    private ArrayList<Double> polarizationApp = new ArrayList<>(0); // Поляризация кажущаяся, %
    private ArrayList<Double> errorPolarizationApp = new ArrayList<>(0); // Погрешность, %

    public int getColumnCnt() {
        return 6;
    }

    public ArrayList<Double> getAmperage() {
        return amperage;
    }

    public ArrayList<Double> getVoltage() {
        return voltage;
    }

    public ArrayList<Double> getResistanceApp() {
        return resistanceApp;
    }

    public ArrayList<Double> getErrorResistanceApp() {
        return errorResistanceApp;
    }

    public ArrayList<Double> getPolarizationApp() {
        return polarizationApp;
    }

    public ArrayList<Double> getErrorPolarizationApp() {
        return errorPolarizationApp;
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

    public void setResistanceApp(ArrayList<Double> resistanceApp) {
        this.resistanceApp = resistanceApp;
    }

    public void setErrorResistanceApp(ArrayList<Double> errorResistanceApp) {
        this.errorResistanceApp = errorResistanceApp;
    }

    public void setPolarizationApp(ArrayList<Double> polarizationApp) {
        this.polarizationApp = polarizationApp;
    }

    public void setErrorPolarizationApp(ArrayList<Double> errorPolarizationApp) {
        this.errorPolarizationApp = errorPolarizationApp;
    }

    public void setSTTFileName(String STTFileName) {
        this.STTFileName = STTFileName;
    }
}

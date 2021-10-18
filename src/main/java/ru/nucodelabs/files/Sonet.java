package ru.nucodelabs.files;

import java.io.File;
import java.util.ArrayList;

public class Sonet {
    private Sonet() {
    } // чтобы нельзя было создать экземпляр класса Sonet

    public static class STTFile {
        private ArrayList<Double> AB_2 = new ArrayList<>(0); // AB/2, м
        private ArrayList<Double> MN_2 = new ArrayList<>(0); // MN/2, м

        public ArrayList<Double> getAB_2() {
            return AB_2;
        }

        public ArrayList<Double> getMN_2() {
            return MN_2;
        }

        public void setAB_2(ArrayList<Double> AB_2) {
            this.AB_2 = AB_2;
        }

        public void setMN_2(ArrayList<Double> MN_2) {
            this.MN_2 = MN_2;
        }
    }

    public static class EXPFile {
        private String STTFileName;

        private String number; // Номер установки
        private String date; // Дата
        private String weather; // Погода
        private String operator; // Оператор
        private String interpreter; // Интерпретатор
        private String checked; // Проверил

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

    public static class MODFile {
        private ArrayList<Double> resistance = new ArrayList<>(0); // Сопротивление, Ом*м
        private ArrayList<Double> polarization = new ArrayList<>(0); // Поляризация, %
        private ArrayList<Double> power = new ArrayList<>(0); // Мощность, м

        public ArrayList<Double> getResistance() {
            return resistance;
        }

        public ArrayList<Double> getPolarization() {
            return polarization;
        }

        public ArrayList<Double> getPower() {
            return power;
        }

        public void setPolarization(ArrayList<Double> polarization) {
            this.polarization = polarization;
        }

        public void setResistance(ArrayList<Double> resistance) {
            this.resistance = resistance;
        }

        public void setPower(ArrayList<Double> power) {
            this.power = power;
        }
    }

    public static Sonet.STTFile readSTT(File file) {
        Sonet.STTFile res = new Sonet.STTFile();
// sample code
        return res;
    }

    public static Sonet.EXPFile readEXP(File file) {
        Sonet.EXPFile res = new Sonet.EXPFile();
// sample code
        return res;
    }

    public static Sonet.MODFile readMOD(File file) {
        Sonet.MODFile res = new Sonet.MODFile();
// sample code
        return res;
    }
}

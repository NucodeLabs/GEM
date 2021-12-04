package ru.nucodelabs.files.sonet;

import java.util.ArrayList;

public class STTFile {
    private ArrayList<Double> AB_2; // AB/2, м
    private ArrayList<Double> MN_2; // MN/2, м

    public STTFile() {
        AB_2 = new ArrayList<>(0);
        MN_2 = new ArrayList<>(0);
    }

    public int getColumnCnt() {
        return 2;
    }

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

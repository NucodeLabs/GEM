package ru.nucodelabs.gem;

import java.util.ArrayList;

public class ForwardSolver {
    private static native double[] solve(
            double[] resistance,
            double[] power,
            int layersCnt,
            double[] AB_2,
            int distCnt);

    public static ArrayList<Double> ves(
            double[] resistance,
            double[] power,
            int layersCnt,
            double[] AB_2,
            int distCnt) {
        ArrayList<Double> res = new ArrayList<>();
        double[] doubleArr = solve(resistance, power, layersCnt, AB_2, distCnt);
        for (double number : doubleArr) {
            res.add(number);
        }
        return res;
    }

    static {
        System.loadLibrary("forwardsolver");
    }
}

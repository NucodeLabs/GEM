package ru.nucodelabs.algorithms;

import java.util.ArrayList;
import java.util.List;

public class ForwardSolver {
    private static native double[] solve(
            double[] resistance,
            double[] power,
            int layersCnt,
            double[] AB_2,
            int distCnt);

    public static List<Double> ves(
            List<Double> resistance,
            List<Double> power,
            List<Double> AB_2) {
        List<Double> res = new ArrayList<>();
        double[] doubleArr = solve(
                resistance.stream().mapToDouble(d -> d).toArray(),
                power.stream().mapToDouble(d -> d).toArray(),
                power.size(),
                AB_2.stream().mapToDouble(d -> d).toArray(),
                AB_2.size());
        for (double number : doubleArr) {
            res.add(number);
        }
        return res;
    }

    static {
        System.loadLibrary("forwardsolver");
    }
}

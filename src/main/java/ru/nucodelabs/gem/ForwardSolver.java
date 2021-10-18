package ru.nucodelabs.gem;

public class ForwardSolver {
    public static native double[] solve(
            double[] resistance,
            double[] power,
            int layersCnt,
            double[] AB_2,
            int distCnt);

    static {
        System.loadLibrary("forwardsolver");
    }
}

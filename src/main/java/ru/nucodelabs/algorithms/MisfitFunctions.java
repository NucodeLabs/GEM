package ru.nucodelabs.algorithms;

public class MisfitFunctions {
    public static native double calculateRelativeDeviation(
            double experimentalResistance,
            double theoreticalResistance
    );

    public static native double calculateRelativeDeviationWithError(
            double experimentalResistance,
            double experimentalErrorResistance,
            double theoreticalResistance
    );

    public static native double calculateResistanceApparent(
            double AB_2,
            double MN_2,
            double voltage,
            double amperage
    );

    static {
        System.loadLibrary("misfit");
    }
}

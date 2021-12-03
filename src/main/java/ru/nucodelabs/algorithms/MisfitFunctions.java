package ru.nucodelabs.algorithms;

public class MisfitFunctions {
    public static native double calculateRelativeDeviation(
            double resistanceExperimental,
            double resistanceTheoretical
    );

    public static native double calculateRelativeDeviationWithError(
            double resistanceExperimental,
            double resistanceExperimentalError,
            double resistanceTheoretical
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

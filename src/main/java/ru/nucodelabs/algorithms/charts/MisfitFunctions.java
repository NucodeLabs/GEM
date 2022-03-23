package ru.nucodelabs.algorithms.charts;

class MisfitFunctions {
    static native double calculateRelativeDeviation(
            double experimentalResistance,
            double theoreticalResistance
    );

    static native double calculateRelativeDeviationWithError(
            double experimentalResistance,
            double experimentalErrorResistance,
            double theoreticalResistance
    );

    static native double calculateResistanceApparent(
            double AB_2,
            double MN_2,
            double voltage,
            double amperage
    );

    static {
        System.loadLibrary("misfit");
    }
}

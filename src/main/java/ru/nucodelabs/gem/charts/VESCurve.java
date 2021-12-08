package ru.nucodelabs.gem.charts;

import javafx.scene.chart.LineChart;
import ru.nucodelabs.data.Picket;

public class VESCurve {
    protected static final int EXP_CURVE_SERIES_CNT = 3;
    protected static final int THEOR_CURVE_SERIES_CNT = 4;
    protected static final int MOD_CURVE_SERIES_CNT = 5;
    protected static final double EPSILON = 1e-6;

    private final LineChart<Double, Double> vesCurve;
    private final Picket picket;

    public VESCurve(LineChart<Double, Double> vesCurve, Picket picket) {
        this.vesCurve = vesCurve;
        this.picket = picket;
    }

    public void createExperimentalCurve() {
        ExperimentalCurve.initializeWithData(vesCurve, picket.getExperimentalData());
    }

    public void createTheoreticalCurve() {
        TheoreticalCurve.initializeWithData(vesCurve, picket.getExperimentalData(), picket.getModelData());
    }

    public void createModelCurve() {
        ModelCurve.initializeWithData(vesCurve, picket.getModelData());
    }

    public Picket getPicket() {
        return picket;
    }
}

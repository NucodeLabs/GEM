package ru.nucodelabs.algorithms.charts;

import ru.nucodelabs.data.ves.ExperimentalMeasurement;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;

final class ExperimentalCurvePointsConverter implements PointsConverter {

    private final List<ExperimentalMeasurement> experimentalData;
    private final List<Double> ab2;
    private final List<Double> resistanceApparent;

    ExperimentalCurvePointsConverter(List<ExperimentalMeasurement> experimentalData) {
        this.experimentalData = experimentalData;
        ab2 = experimentalData.stream().map(ExperimentalMeasurement::getAb2).toList();
        resistanceApparent = experimentalData.stream().map(ExperimentalMeasurement::getResistanceApparent).toList();
    }

    @Override
    public List<Point> points() {
        if (experimentalData.size() == 0) {
            return new ArrayList<>();
        }

        List<Point> points = new ArrayList<>();

        for (int i = 0; i < experimentalData.size(); i++) {
            double dotX = ab2.get(i);
            double dotY = max(resistanceApparent.get(i), 0);

            points.add(new Point(dotX, dotY));
        }

        return points;
    }

    // +++++++++++++++++++++++++++++++++++++++ ERROR POINTS +++++++++++++++++++++++++++++++++++++++++++++++
    private enum BoundType {UPPER_BOUND, LOWER_BOUND}

    static final class UpperBoundErrorPointsConverter implements PointsConverter {

        private final List<ExperimentalMeasurement> experimentalData;

        UpperBoundErrorPointsConverter(List<ExperimentalMeasurement> experimentalData) {
            this.experimentalData = experimentalData;
        }

        @Override
        public List<Point> points() {
            return new ErrorPointsConverter(BoundType.UPPER_BOUND, experimentalData).points();
        }
    }

    static final class LowerBoundErrorPointsConverter implements PointsConverter {

        private final List<ExperimentalMeasurement> experimentalData;

        LowerBoundErrorPointsConverter(List<ExperimentalMeasurement> experimentalData) {
            this.experimentalData = experimentalData;
        }

        @Override
        public List<Point> points() {
            return new ErrorPointsConverter(BoundType.LOWER_BOUND, experimentalData).points();
        }
    }

    private static final class ErrorPointsConverter implements PointsConverter {
        private final BoundType boundType;
        private final List<ExperimentalMeasurement> experimentalData;
        private final List<Double> errorResistanceApparent;
        private final List<Double> resistanceApparent;
        private final List<Double> ab2;

        ErrorPointsConverter(BoundType boundType, List<ExperimentalMeasurement> experimentalData) {
            this.boundType = boundType;
            this.experimentalData = experimentalData;
            errorResistanceApparent = experimentalData.stream().map(ExperimentalMeasurement::geErrorResistanceApparent).toList();
            resistanceApparent = experimentalData.stream().map(ExperimentalMeasurement::getResistanceApparent).toList();
            ab2 = experimentalData.stream().map(ExperimentalMeasurement::getAb2).toList();
        }

        @Override
        public List<Point> points() {
            if (experimentalData.size() == 0) {
                return new ArrayList<>();
            }

            List<Point> points = new ArrayList<>();

            for (int i = 0; i < experimentalData.size(); i++) {
                double dotX = ab2.get(i);
                double error = errorResistanceApparent.get(i) / 100f;
                double dotY;
                if (boundType == BoundType.UPPER_BOUND) {
                    dotY = max(
                            resistanceApparent.get(i)
                                    + resistanceApparent.get(i) * error, 0);
                } else {
                    dotY = max(
                            resistanceApparent.get(i)
                                    - resistanceApparent.get(i) * error, 0);
                }

                points.add(new Point(dotX, dotY));
            }

            return points;
        }
    }
}

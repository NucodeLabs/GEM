package ru.nucodelabs.gem.view;

import javafx.beans.property.DoubleProperty;

public class VESCurvesNavigator {

    private final double CHANGE_VALUE;
    private final DoubleProperty vesCurvesXLowerBound;
    private final DoubleProperty vesCurvesXUpperBound;
    private final DoubleProperty vesCurvesYLowerBound;
    private final DoubleProperty vesCurvesYUpperBound;

    public VESCurvesNavigator(
            DoubleProperty vesCurvesXLowerBound,
            DoubleProperty vesCurvesXUpperBound,
            DoubleProperty vesCurvesYLowerBound,
            DoubleProperty vesCurvesYUpperBound,
            double changeValue) {
        this.vesCurvesXLowerBound = vesCurvesXLowerBound;
        this.vesCurvesXUpperBound = vesCurvesXUpperBound;
        this.vesCurvesYLowerBound = vesCurvesYLowerBound;
        this.vesCurvesYUpperBound = vesCurvesYUpperBound;
        this.CHANGE_VALUE = changeValue;
    }

    public void zoomIn() {
        if (vesCurvesXLowerBound.get() < vesCurvesXUpperBound.get()
                && vesCurvesYLowerBound.get() < vesCurvesYUpperBound.get()) {
            vesCurvesXLowerBound.set(vesCurvesXLowerBound.get() + CHANGE_VALUE);
            vesCurvesXUpperBound.set(vesCurvesXUpperBound.get() - CHANGE_VALUE);

            vesCurvesYLowerBound.set(vesCurvesYLowerBound.get() + CHANGE_VALUE);
            vesCurvesYUpperBound.set(vesCurvesYUpperBound.get() - CHANGE_VALUE);
        }
    }

    public void zoomOut() {
        vesCurvesXLowerBound.set(vesCurvesXLowerBound.get() - CHANGE_VALUE);
        vesCurvesXUpperBound.set(vesCurvesXUpperBound.get() + CHANGE_VALUE);

        vesCurvesYLowerBound.set(vesCurvesYLowerBound.get() - CHANGE_VALUE);
        vesCurvesYUpperBound.set(vesCurvesYUpperBound.get() + CHANGE_VALUE);
    }

    public void moveRight() {
        vesCurvesXLowerBound.set(vesCurvesXLowerBound.get() + CHANGE_VALUE);
        vesCurvesXUpperBound.set(vesCurvesXUpperBound.get() + CHANGE_VALUE);
    }

    public void moveLeft() {
        vesCurvesXLowerBound.set(vesCurvesXLowerBound.get() - CHANGE_VALUE);
        vesCurvesXUpperBound.set(vesCurvesXUpperBound.get() - CHANGE_VALUE);
    }

    public void moveUp() {
        vesCurvesYLowerBound.set(vesCurvesYLowerBound.get() + CHANGE_VALUE);
        vesCurvesYUpperBound.set(vesCurvesYUpperBound.get() + CHANGE_VALUE);
    }

    public void moveDown() {
        vesCurvesYLowerBound.set(vesCurvesYLowerBound.get() - CHANGE_VALUE);
        vesCurvesYUpperBound.set(vesCurvesYUpperBound.get() - CHANGE_VALUE);
    }
}

package ru.nucodelabs.data.ves;

import java.util.ArrayList;
import java.util.List;

class VesUtils {
    private VesUtils() {
    }

    static List<Double> zOfPower(List<ModelLayer> modelData, double picketZ) {
        List<Double> heightList = new ArrayList<>();
        List<Double> power = modelData.stream().map(ModelLayer::getPower).toList();

        double sum = picketZ;
        for (var p : power) {
            sum -= p;
            heightList.add(sum);
        }

        // последняя уходит в бесконечность
        return heightList;
    }

    static double xOfPicket(Section section, int index) {

        double x = 0;
        for (int i = 0; i <= index; i++) {
            x += section.getPickets().get(i).getOffsetX();
        }

        return x;
    }

    static double resistanceApparent(double ab2, double mn2, double amperage, double voltage) {
        return k(ab2, mn2) * (voltage / amperage);
    }

    private static double k(double ab2, double mn2) {
        double am = ab2 - mn2;
        double bm = ab2 + mn2;
        double an = bm;
        double bn = am;
        return (2 * Math.PI)
                / (1 / am
                - 1 / bm
                - 1 / an
                + 1 / bn);
    }
}

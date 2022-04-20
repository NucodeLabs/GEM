package ru.nucodelabs.data.ves;

import java.util.ArrayList;
import java.util.List;

public class VesUtils {
    private VesUtils() {
    }

    public static List<Double> zOfPower(List<ModelLayer> modelData, double picketZ) {
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

    public static double xOfPicket(Section section, int index) {

        double x = 0;
        for (int i = 0; i <= index; i++) {
            x += section.getPickets().get(i).getOffsetX();
        }

        return x;
    }
}

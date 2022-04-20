package ru.nucodelabs.data.ves;

import java.util.ArrayList;
import java.util.List;

public class VesUtils {
    private VesUtils() {
    }

    public static List<Double> zOfPower(List<ModelLayer> modelData, double z) {
        List<Double> heightList = new ArrayList<>();
        List<Double> power = modelData.stream().map(ModelLayer::getPower).toList();

        double sum = z;
        for (var p : power) {
            sum -= p;
            heightList.add(sum);
        }

        // последняя уходит в бесконечность
        return heightList;
    }
}

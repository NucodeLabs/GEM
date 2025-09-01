package ru.nucodelabs.algorithms;

import org.junit.jupiter.api.Test;
import smile.interpolation.RBFInterpolation2D;
import smile.math.rbf.GaussianRadialBasis;
import smile.math.rbf.MultiquadricRadialBasis;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RBFTest {
    @Test
    public void test1() {
        double[] x1 = {0.0, 1.0, 2.0, 4.0, 0.0, 1.0, 4.0, 0.0, 1.0, 2.0, 4.0};
        double[] x2 = {10.0, 10.0, 10.0, 10.0, 20.0, 20.0, 20.0, 30.0, 30.0, 30.0, 30.0};
        double[] y = {100.0, 110.0, 140.0, 180.0, 220.0, 210.0, 220.0, 300.0, 300.0, 320.0, 310.0};
        RBFInterpolation2D interpolation2D = new RBFInterpolation2D(x1, x2, y, new GaussianRadialBasis());

        double value = interpolation2D.interpolate(2.0, 20.0);
        System.out.println(value);
    }

    @Test
    public void test2() {
        double[] x1 = {1.5, 2.0, 3.0, 4.0, 5.0, 7.0, 9.0, 12.0, 15.0, 15.0, 20.0, 20.0, 32.0, 40.0, 50.0, 65.0, 65.0, 80.0, 80.0, 100.0, 123.0, 1.5, 2.0, 3.0, 4.0, 5.0, 7.0, 9.0, 12.0, 15.0, 15.0, 20.0, 20.0, 25.0, 32.0, 40.0, 50.0, 65.0, 65.0, 80.0, 80.0, 100.0, 123.0, 1.5, 2.0, 3.0, 4.0, 5.0, 7.0, 9.0, 12.0, 15.0, 15.0, 20.0, 20.0, 25.0, 32.0, 40.0, 50.0, 65.0, 65.0, 80.0, 80.0, 100.0, 123.0};
        double[] x2 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 200.0, 200.0, 200.0, 200.0, 200.0, 200.0, 200.0, 200.0, 200.0, 200.0, 200.0, 200.0, 200.0, 200.0, 200.0, 200.0, 200.0, 200.0, 200.0, 200.0, 200.0, 200.0, 300.0, 300.0, 300.0, 300.0, 300.0, 300.0, 300.0, 300.0, 300.0, 300.0, 300.0, 300.0, 300.0, 300.0, 300.0, 300.0, 300.0, 300.0, 300.0, 300.0, 300.0, 300.0};
        double[] y = {135.01, 130.3, 151.19, 158.34, 169.54, 159.71, 142.06, 142.71, 124.9, 125.4, 142.92, 128.77, 116.4, 106.52, 103.02, 103.23, 96.156, 107.29, 100.73, 113.53, 125.54, 221.09, 163.79, 166.89, 170.74, 179.45, 176.85, 167.28, 143.03, 130.18, 130.5, 123.0, 122.67, 110.11, 105.11, 95.513, 95.813, 95.723, 95.723, 97.983, 100.57, 114.42, 131.75, 138.98, 137.74, 162.18, 173.18, 179.36, 169.39, 159.82, 139.85, 120.48, 117.94, 109.26, 98.269, 94.793, 92.366, 92.135, 96.517, 99.337, 99.337, 123.81, 127.55, 139.81, 161.68};

        List<Double> listX1 = new ArrayList<>();
        for (var d:x1) {
            listX1.add(d);
        }
        List<Double> listX2 = new ArrayList<>();
        for (var d:x2) {
            listX2.add(d);
        }
        List<Double> listY = new ArrayList<>();
        for (var d:y) {
            listY.add(d);
        }

        for (int i = 0; i < listX1.size() - 1; i++) {
            if (Objects.equals(listX1.get(i), listX1.get(i + 1))) {
                listX1.remove(i);
                listX2.remove(i);
                listY.remove(i);
            }
        }

        //25, 0, 116.78

        double[] nx1 = listX1.stream().mapToDouble(Double::doubleValue).toArray();
        double[] nx2 = listX2.stream().mapToDouble(Double::doubleValue).toArray();
        double[] ny = listY.stream().mapToDouble(Double::doubleValue).toArray();
        RBFInterpolation2D interpolation2D = new RBFInterpolation2D(nx1, nx2, ny, new MultiquadricRadialBasis());

        double value = interpolation2D.interpolate(25.0, 500.0);
        System.out.println(value);
    }
}

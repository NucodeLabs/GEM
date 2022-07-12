package ru.nucodelabs.algorithms;

import org.junit.jupiter.api.Test;
import org.tinfour.common.Vertex;
import org.tinfour.interpolation.TriangularFacetInterpolator;
import org.tinfour.standard.IncrementalTin;

public class TinTest {
    @Test
    public void test1() {
        Vertex v1 = new Vertex(0.0, 0.0, 100);
        Vertex v2 = new Vertex(0.0, 1.0, 100);
        Vertex v3 = new Vertex(1.0, 0.0, 100);
        Vertex v4 = new Vertex(1.0, 1.0, 100);
        Vertex v5 = new Vertex(10.0, 10.0, 500);

        IncrementalTin tin = new IncrementalTin(1.0);
        tin.add(v1);
        tin.add(v2);
        tin.add(v3);
        tin.add(v4);
        tin.add(v5);
        TriangularFacetInterpolator inTri = new TriangularFacetInterpolator(tin);

        double value = inTri.interpolate(2.5, 2.5, null);
        System.out.println(value);
    }
}

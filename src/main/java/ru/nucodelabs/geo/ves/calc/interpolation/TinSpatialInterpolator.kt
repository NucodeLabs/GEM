package ru.nucodelabs.geo.ves.calc.interpolation

import org.tinfour.common.Vertex
import org.tinfour.interpolation.IInterpolatorOverTin
import org.tinfour.interpolation.TriangularFacetInterpolator
import org.tinfour.standard.IncrementalTin

class TinSpatialInterpolator(x: DoubleArray, y: DoubleArray, f: DoubleArray): SpatialInterpolator {

    private val inTri: IInterpolatorOverTin

    init {
        val tin = IncrementalTin()
        for (i in x.indices) {
            tin.add(Vertex(x[i], y[i], f[i]))
        }
        inTri = TriangularFacetInterpolator(tin)
    }
    override fun interpolate(x: Double, y: Double): Double {
        return inTri.interpolate(x, y, null)
    }

}

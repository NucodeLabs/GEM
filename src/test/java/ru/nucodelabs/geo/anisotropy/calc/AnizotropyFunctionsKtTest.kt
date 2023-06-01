package ru.nucodelabs.geo.anisotropy.calc

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.nucodelabs.geo.anisotropy.*
import ru.nucodelabs.mathves.AnizotropyFunctions
import java.util.*

class AnizotropyFunctionsKtTest {
    @Test
    fun forwardSolveAnizotropy() {
        val n_azimuth: Short = 1
        val signals_azimuth = doubleArrayOf(0.0)
        val AB2 = doubleArrayOf(
            7.5, 12.5, 17.5, 22.5, 27.5, 32.5, 37.5, 42.5, 47.5, 52.5, 57.5, 62.5, 67.5, 72.5, 77.5,
            82.5, 87.5, 92.5, 97.5, 102.5, 107.5, 112.5, 117.5
        )
        val MN2 = DoubleArray(AB2.size)
        Arrays.fill(MN2, 5.0)
        val idx_azimuth = ShortArray(AB2.size)
        Arrays.fill(idx_azimuth, 1.toShort())
        val n_signals = AB2.size.toShort()
        val signals = DoubleArray(n_signals.toInt())
        val n_layers: Short = 3
        val h = doubleArrayOf(117.5 / 12.0, 117.5 / 12.0, Double.NaN)
        val ro_avg = doubleArrayOf(27.0, 40.0, 285.0)
        val kanisotropy_vert = doubleArrayOf(0.0, 0.0, 0.0)
        val azimuth = doubleArrayOf(0.0, 0.0, 0.0)
        val kanisotropy_azimuth = doubleArrayOf(0.0, 0.0, 0.0)

        val azimuthSignals = arrayListOf<AzimuthSignals>()
        azimuthSignals.add(
            AzimuthSignals(
                signals_azimuth[0],
                Signals(
                    arrayListOf<Signal>().apply {
                        for (i in 0 until n_signals) {
                            add(
                                Signal(
                                    AB2[i],
                                    MN2[i],
                                    0.0,
                                    0.0,
                                    0.0
                                )
                            )
                        }
                    }
                )
            )
        )

        val model = arrayListOf<ModelLayer>()
        for (i in 0 until n_layers) {
            model.add(
                ModelLayer(
                    FixableValue(h[i], false),
                    FixableValue(ro_avg[i], false),
                    FixableValue(kanisotropy_vert[i], false),
                    FixableValue(azimuth[i], false),
                    FixableValue(kanisotropy_azimuth[i], false)
                )
            )
        }

        val signalsOut = forwardSolveAnizotropy(azimuthSignals, model)
        println(signalsOut)

        AnizotropyFunctions.signalModelingWithAzimuthSchlumberger(
            n_azimuth,
            signals_azimuth,
            n_signals,
            idx_azimuth,
            AB2,
            MN2,
            signals,
            n_layers,
            h,
            ro_avg,
            kanisotropy_vert,
            azimuth,
            kanisotropy_azimuth
        )

        Assertions.assertArrayEquals(signals, signalsOut.toDoubleArray())
    }

    @Test
    fun forwardSolveAnizotropy1() {
        val n_azimuth: Short = 2
        val signals_azimuth = doubleArrayOf(0.0, 90.0)
        val AB2 = doubleArrayOf(
            7.5, 12.5, 17.5, 22.5, 27.5, 32.5, 37.5, 42.5, 47.5, 52.5, 57.5, 62.5, 67.5, 72.5, 77.5,
            82.5, 87.5, 92.5, 97.5, 102.5, 107.5, 112.5, 117.5,
            7.5, 12.5, 17.5, 22.5, 27.5, 32.5, 37.5, 42.5, 47.5, 52.5, 57.5, 62.5, 67.5, 72.5, 77.5,
            82.5, 87.5, 92.5, 97.5, 102.5, 107.5, 112.5, 117.5
        )
        val MN2 = DoubleArray(AB2.size)
        Arrays.fill(MN2, 5.0)
        val idx_azimuth = ShortArray(AB2.size)
        Arrays.fill(idx_azimuth, 1.toShort())
        for (i in AB2.size / 2 until AB2.size) {
            MN2[i] = 6.0
            idx_azimuth[i] = 2.toShort()
        }
        val n_signals = AB2.size.toShort()
        val signals = DoubleArray(n_signals.toInt())
        val n_layers: Short = 3
        val h = doubleArrayOf(117.5 / 12.0, 117.5 / 12.0, Double.NaN)
        val ro_avg = doubleArrayOf(27.0, 40.0, 285.0)
        val kanisotropy_vert = doubleArrayOf(0.2, 0.3, 0.4)
        val azimuth = doubleArrayOf(0.0, 45.0, 0.0)
        val kanisotropy_azimuth = doubleArrayOf(1.0, 2.0, 1.0)

        val azimuthSignals = arrayListOf<AzimuthSignals>()
        azimuthSignals.add(
            AzimuthSignals(
                signals_azimuth[0],
                Signals(
                    arrayListOf<Signal>().apply {
                        for (i in 0 until n_signals / 2) {
                            add(
                                Signal(
                                    AB2[i],
                                    MN2[i],
                                    0.0,
                                    0.0,
                                    0.0
                                )
                            )
                        }
                    }
                )
            )
        )
        azimuthSignals.add(
            AzimuthSignals(
                signals_azimuth[1],
                Signals(
                    arrayListOf<Signal>().apply {
                        for (i in n_signals / 2 until n_signals) {
                            add(
                                Signal(
                                    AB2[i],
                                    MN2[i],
                                    0.0,
                                    0.0,
                                    0.0
                                )
                            )
                        }
                    }
                )
            )
        )

        val model = arrayListOf<ModelLayer>()
        for (i in 0 until n_layers) {
            model.add(
                ModelLayer(
                    FixableValue(h[i], false),
                    FixableValue(ro_avg[i], false),
                    FixableValue(kanisotropy_vert[i], false),
                    FixableValue(azimuth[i], false),
                    FixableValue(kanisotropy_azimuth[i], false)
                )
            )
        }

        val signalsOut = forwardSolveAnizotropy(azimuthSignals, model)
        println(signalsOut)

        AnizotropyFunctions.signalModelingWithAzimuthSchlumberger(
            n_azimuth,
            signals_azimuth,
            n_signals,
            idx_azimuth,
            AB2,
            MN2,
            signals,
            n_layers,
            h,
            ro_avg,
            kanisotropy_vert,
            azimuth,
            kanisotropy_azimuth
        )

        Assertions.assertArrayEquals(signals, signalsOut.toDoubleArray())
    }
}
package ru.nucodelabs.files.sonet

import java.io.File
import java.util.*

internal object SonetImportUtils {
    @JvmStatic
    @Throws(Exception::class)
    fun readSTT(file: File): STTFile {
        val res = STTFile()
        Scanner(file).useLocale(Locale.US).use { sc ->
            val numbers = columnReader(sc, STTFile().columnCnt)
            res.AB_2.addAll(numbers.map { it[0] })
            res.MN_2.addAll(numbers.map { it[1] })
        }
        res.file = file
        return res
    }

    @JvmStatic
    @Throws(Exception::class)
    fun readEXP(file: File): EXPFile {
        val res = EXPFile()
        Scanner(file, "Cp866").useLocale(Locale.US).use { sc ->
            res.STTFileName = sc.nextLine()
            readPassport(sc, res)
            val numbers = if (sc.hasNext("\\$") ) {
                sc.next(); columnReader(sc, EXPFile().columnCnt)
            } else null
            requireNotNull(numbers)
            res.amperage.addAll(numbers.map { it[0] })
            res.voltage.addAll(numbers.map { it[1] })
            res.resistanceApparent.addAll(numbers.map { it[2] })
            res.errorResistanceApparent.addAll(numbers.map { it[3] })
            res.polarizationApparent.addAll(numbers.map { it[4] })
            res.errorPolarizationApparent.addAll(numbers.map { it[5] })
        }
        res.file = file
        return res
    }

    @JvmStatic
    @Throws(Exception::class)
    fun readMOD(file: File): MODFile {
        val res = MODFile()
        Scanner(file).useLocale(Locale.US).use { sc ->
            val numbers = columnReader(sc, MODFile().columnCnt)
            res.resistance.addAll(numbers.map { it[0] })
            res.power.addAll(numbers.map { it[1] })
            res.polarization.addAll(numbers.map { it[2] })
        }
        res.file = file
        return res
    }

    private fun readPassport(sc: Scanner, res: EXPFile) {
        val strList = ArrayList<String>()
        while (sc.hasNextLine() && !sc.hasNext("\\$") && strList.size < 6) {
            strList.add(sc.nextLine())
        }
        if (strList.size > 0) res.number = strList[0]
        if (strList.size > 1) res.date = strList[1]
        if (strList.size > 2) res.weather = strList[2]
        if (strList.size > 3) res.operator = strList[3]
        if (strList.size > 4) res.interpreter = strList[4]
        if (strList.size > 5) res.checked = strList[5]
    }

    private fun columnReader(sc: Scanner, colCnt: Int): MutableList<MutableList<Double>> {
        val res = mutableListOf<MutableList<Double>>()
        while (sc.hasNextLine() && !sc.hasNext("\\$") && !sc.hasNext("-1.0")) {
            val line = sc.nextLine()
            if (line.isBlank()) continue
            Scanner(line).useLocale(Locale.US).use { rowScanner ->
                val row = MutableList(colCnt) { if (rowScanner.hasNextDouble()) rowScanner.nextDouble() else 0.0 }
                res.add(row)
            }
        }
        return res
    }
}

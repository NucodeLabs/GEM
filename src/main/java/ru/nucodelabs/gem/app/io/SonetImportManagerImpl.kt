package ru.nucodelabs.gem.app.io

import ru.nucodelabs.files.sonet.*
import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.geo.ves.ModelLayer
import ru.nucodelabs.geo.ves.Picket
import java.io.File

internal class SonetImportManagerImpl : SonetImportManager {

    private fun parseExpWithStt(expFile: File): Pair<EXPFile, STTFile> {
        val expParsed = EXPFileParser(expFile).parse()
        val expFilePath = expFile.toPath()
        return expParsed to STTFileParser(
            File("${expFilePath.parent}${File.separator}${expParsed.sttFileName}")
        ).parse()
    }

    private fun parseExperimentalDataAndComment(expFile: File): Pair<List<ExperimentalData>, String> {
        val (expParsed, sttParsed) = parseExpWithStt(expFile)

        val minSize = listOf(
            sttParsed.aB_2,
            sttParsed.mN_2,
            expParsed.amperage,
            expParsed.voltage,
            expParsed.resistivityApparent,
            expParsed.errorResistivityApparent
        ).minOf { it.size }

        val expData: MutableList<ExperimentalData> = mutableListOf()
        for (i in 0 until minSize) {
            expData += ExperimentalData(
                ab2 = sttParsed.aB_2[i],
                mn2 = sttParsed.mN_2[i],
                resistivityApparent = expParsed.resistivityApparent[i],
                errorResistivityApparent = expParsed.errorResistivityApparent[i],
                amperage = expParsed.amperage[i],
                voltage = expParsed.voltage[i]
            )
        }
        return expData to passportAsString(expParsed)
    }

    private fun passportAsString(expFile: EXPFile): String {
        return """
            Номер установки: ${expFile.number}
            Дата: ${expFile.date}
            Погода: ${expFile.weather}
            Оператор: ${expFile.operator}
            Интерпретатор: ${expFile.interpreter}
            Проверил ${expFile.checked}
        """.trimIndent()
    }

    override fun fromEXPFile(expFile: File): Picket {
        val fileName = expFile.name

        val picketName = if (fileName.endsWith(".exp", ignoreCase = true)) {
            fileName.substring(0, fileName.length - ".exp".length)
        } else {
            fileName
        }

        val (expData, passport) = parseExperimentalDataAndComment(expFile)
        return Picket(
            name = picketName,
            experimentalData = expData,
            comment = passport
        )
    }

    override fun fromMODFile(modFile: File): List<ModelLayer> {
        val modParsed = MODFileParser(modFile).parse()

        val minSize = listOf(
            modParsed.power,
            modParsed.resistivity
        ).minOf { it.size }

        val modelData: MutableList<ModelLayer> = mutableListOf()
        for (i in 0 until minSize) {
            modelData += ModelLayer(
                power = modParsed.power[i],
                resistivity = modParsed.resistivity[i]
            )
        }
        return modelData
    }
}
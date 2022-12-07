package ru.nucodelabs.gem.app.io

import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.geo.ves.ModelLayer
import ru.nucodelabs.geo.ves.Picket
import ru.nucodelabs.files.sonet.*
import java.io.File

internal class SonetImportManagerImpl : SonetImportManager {

    private fun parseExpWithStt(expFile: File): Pair<EXPFile, STTFile> {
        val expParsed = EXPFileParser(expFile).parse()
        val expFilePath = expFile.toPath()
        return expParsed to STTFileParser(
            File("${expFilePath.parent}${File.separator}${expParsed.sttFileName}")
        ).parse()
    }

    private fun parseExperimentalData(expFile: File): List<ExperimentalData> {
        val (expParsed, sttParsed) = parseExpWithStt(expFile)

        val minSize = listOf(
            sttParsed.aB_2,
            sttParsed.mN_2,
            expParsed.amperage,
            expParsed.voltage,
            expParsed.resistanceApparent,
            expParsed.errorResistanceApparent
        ).minOf { it.size }

        val expData: MutableList<ExperimentalData> = mutableListOf()
        for (i in 0 until minSize) {
            expData += ExperimentalData(
                ab2 = sttParsed.aB_2[i],
                mn2 = sttParsed.mN_2[i],
                resistanceApparent = expParsed.resistanceApparent[i],
                errorResistanceApparent = expParsed.errorResistanceApparent[i],
                amperage = expParsed.amperage[i],
                voltage = expParsed.voltage[i]
            )
        }
        return expData
    }

    override fun fromEXPFile(expFile: File): Picket {
        val fileName = expFile.name

        val picketName = if (fileName.endsWith(".exp", ignoreCase = true)) {
            fileName.substring(0, fileName.length - ".exp".length)
        } else {
            fileName
        }

        return Picket(
            name = picketName,
            experimentalData = parseExperimentalData(expFile)
        )
    }

    override fun fromMODFile(modFile: File): List<ModelLayer> {
        val modParsed = MODFileParser(modFile).parse()

        val minSize = listOf(
            modParsed.power,
            modParsed.resistance
        ).minOf { it.size }

        val modelData: MutableList<ModelLayer> = mutableListOf()
        for (i in 0 until minSize) {
            modelData += ModelLayer(
                power = modParsed.power[i],
                resistance = modParsed.resistance[i]
            )
        }
        return modelData
    }
}
package ru.nucodelabs.gem.app.io

import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.data.ves.ModelLayer
import ru.nucodelabs.data.ves.Picket
import ru.nucodelabs.files.sonet.*
import java.io.File

internal class SonetImportManagerImpl : SonetImportManager {
    @Throws(Exception::class)
    override fun loadNameAndExperimentalDataFromEXPFile(expFile: File, target: Picket): Picket {
        val fileName = expFile.name

        val picketName = if (fileName.endsWith(".exp", ignoreCase = true)) {
            fileName.substring(0, fileName.length - ".exp".length)
        } else {
            fileName
        }

        return loadExperimentalDataFromEXPFile(expFile, target).withName(picketName)
    }

    @Throws(Exception::class)
    private fun parseExpWithStt(expFile: File): Pair<EXPFile, STTFile> {
        val expParsed = EXPFileParser(expFile).parse()
        val expFilePath = expFile.toPath()
        return expParsed to STTFileParser(
            File("${expFilePath.parent}${File.separator}${expParsed.sttFileName}")
        ).parse()
    }

    @Throws(Exception::class)
    override fun loadExperimentalDataFromEXPFile(expFile: File, target: Picket): Picket {
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
            expData += ExperimentalData.create(
                sttParsed.aB_2[i],
                sttParsed.mN_2[i],
                expParsed.resistanceApparent[i],
                expParsed.errorResistanceApparent[i],
                expParsed.amperage[i],
                expParsed.voltage[i]
            )
        }

        return target.withExperimentalData(expData)
    }

    @Throws(Exception::class)
    override fun loadModelDataFromMODFile(modFile: File, target: Picket): Picket {
        val modParsed = MODFileParser(modFile).parse()

        val minSize = listOf(
            modParsed.power,
            modParsed.resistance
        ).minOf { it.size }

        val modelData: MutableList<ModelLayer> = mutableListOf()
        for (i in 0 until minSize) {
            modelData += ModelLayer.createNotFixed(
                modParsed.power[i],
                modParsed.resistance[i]
            )
        }
        return target.withModelData(modelData)
    }
}
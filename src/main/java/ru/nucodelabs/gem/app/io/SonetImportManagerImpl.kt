package ru.nucodelabs.gem.app.io

import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.data.ves.ModelLayer
import ru.nucodelabs.data.ves.Picket
import ru.nucodelabs.files.sonet.EXPFileParser
import ru.nucodelabs.files.sonet.MODFileParser
import ru.nucodelabs.files.sonet.STTFile
import ru.nucodelabs.files.sonet.STTFileParser
import java.io.File

internal class SonetImportManagerImpl : SonetImportManager {
    @Throws(Exception::class)
    override fun loadNameAndExperimentalDataFromEXPFile(expFile: File, target: Picket): Picket {
        val picketName: String
        val fileName = expFile.name
        picketName = if (fileName.endsWith(".EXP") || fileName.endsWith(".exp")) {
            fileName.substring(0, fileName.length - ".EXP".length)
        } else {
            fileName
        }
        return loadExperimentalDataFromEXPFile(expFile, target).withName(picketName)
    }

    @Throws(Exception::class)
    private fun getSTTFile(expFile: File): STTFile {
        val expFile1 = EXPFileParser(expFile).parse()
        val expFilePath = expFile.toPath()
        return STTFileParser(
            File(
                expFilePath.parent.toString()
                        + File.separator
                        + expFile1.sttFileName
            )
        ).parse()
    }

    @Throws(Exception::class)
    override fun loadExperimentalDataFromEXPFile(expFile: File, target: Picket): Picket {
        val expFile1 = EXPFileParser(expFile).parse()
        val sttFile = getSTTFile(expFile)
        val sizes = listOf(
            sttFile.aB_2,
            sttFile.mN_2,
            expFile1.amperage,
            expFile1.voltage,
            expFile1.resistanceApparent,
            expFile1.errorResistanceApparent
        ).map { it.size }
        val minSize = sizes.min()
        val expData: MutableList<ExperimentalData> = mutableListOf()
        for (i in 0 until minSize) {
            expData.add(
                ExperimentalData.create(
                    sttFile.aB_2[i],
                    sttFile.mN_2[i],
                    expFile1.resistanceApparent[i],
                    expFile1.errorResistanceApparent[i],
                    expFile1.amperage[i],
                    expFile1.voltage[i]
                )
            )
        }
        return target.withExperimentalData(expData)
    }

    @Throws(Exception::class)
    override fun loadModelDataFromMODFile(modFile: File, target: Picket): Picket {
        val modFile1 = MODFileParser(modFile).parse()
        val sizes = listOf(
            modFile1.power,
            modFile1.resistance
        ).map { it.size }
        val minSize = sizes.min()
        val modelData: MutableList<ModelLayer> = mutableListOf()
        for (i in 0 until minSize) {
            modelData.add(
                ModelLayer.create(
                    modFile1.power[i],
                    modFile1.resistance[i]
                )
            )
        }
        return target.withModelData(modelData)
    }
}
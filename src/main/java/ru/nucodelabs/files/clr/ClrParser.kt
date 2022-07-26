package ru.nucodelabs.files.clr

import javafx.scene.paint.Color
import ru.nucodelabs.gem.view.tables.TextToTableParser
import java.io.File

class ClrParser(
    file: File
) {

    private val parsedTable by TextToTableParser(file.readText())::parsedTable
    val name = parsedTable[0][0] ?: ""

    val colorNodes by lazy {
        val nodes = mutableListOf<ColorNode>()
        for (i in 1 until parsedTable.size) {
            nodes += ColorNode(
                (parsedTable[i][0]?.toDouble() ?: throw IllegalStateException()) / 100.0,
                Color.rgb(
                    parsedTable[i][1]?.toInt() ?: throw IllegalStateException(),
                    parsedTable[i][2]?.toInt() ?: throw IllegalStateException(),
                    parsedTable[i][3]?.toInt() ?: throw IllegalStateException(),
                    (parsedTable[i].getOrNull(4)?.toDouble() ?: 255.0) / 255.0
                )
            )
        }

        return@lazy nodes
    }
}
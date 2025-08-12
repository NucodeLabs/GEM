package ru.nucodelabs.files.clr

import ru.nucodelabs.util.TextToTableParser

class ClrParser(
    clrFileContent: String
) {

    private val parsedTable by TextToTableParser(clrFileContent)::parsedTable
    val name = parsedTable[0][0] ?: ""

    val colorNodes by lazy {
        val nodes = mutableListOf<ColorNode>()
        for (i in 1 until parsedTable.size) {
            nodes += ColorNode(
                (parsedTable[i][0]?.toDouble() ?: throw IllegalStateException()) / 100.0,
                RgbaColor(
                    parsedTable[i][1]?.toInt() ?: throw IllegalStateException(),
                    parsedTable[i][2]?.toInt() ?: throw IllegalStateException(),
                    parsedTable[i][3]?.toInt() ?: throw IllegalStateException(),
                    parsedTable[i].getOrNull(4)?.toInt() ?: 255
                )
            )
        }

        return@lazy nodes
    }
}
package ru.nucodelabs.gem.view.tables

class TextToTableParser(
    text: String
) {
    init {
        if (text.isBlank()) {
            throw IllegalArgumentException("String is blank")
        }
    }

    val parsedTable by lazy { convertToNullableArray(parseTabulation(text)) }
    val columnsCount by lazy { parsedTable.size }
    val rowsCount by lazy { parsedTable[0].size }

    private fun convertToNullableArray(rows: List<List<String>>): Array<Array<String?>> {
        val maxRowLength = rows.maxOf { it.size }
        return Array(rows.size) { rIdx -> Array(maxRowLength) { cIdx -> rows[rIdx].getOrNull(cIdx) } }
    }

    private fun parseTabulation(text: String): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        for (row in text.split("\n").filter { it.isNotBlank() }) {

            rows += row.split(regex = "\\s+".toRegex()).filter { it.isNotBlank() }

        }
        return rows
    }
}
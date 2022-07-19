package ru.nucodelabs.gem.view.tables

class TextToTableParser(
    text: String
) {
    val parsedTable by lazy { parseTabulation(text) }

    fun parseTabulation(text: String): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        for (row in text.split("\n")) {
            rows += row.split(regex = "\\s+".toRegex())
        }
        return rows
    }
}
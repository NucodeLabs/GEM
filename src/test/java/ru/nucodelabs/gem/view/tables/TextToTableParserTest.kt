package ru.nucodelabs.gem.view.tables

import org.junit.jupiter.api.Test

internal class TextToTableParserTest {
    @Test
    fun parse() {
        val testString = "123\t12\t123\n" +
                "3212\t4512\t43"

        val textToTableParser = TextToTableParser(testString)
        val result = textToTableParser.parsedTable
    }
}
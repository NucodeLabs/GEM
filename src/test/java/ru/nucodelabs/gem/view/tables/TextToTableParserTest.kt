package ru.nucodelabs.gem.view.tables

import org.junit.jupiter.api.Test

internal class TextToTableParserTest {
    @Test
    fun parse() {
        val testString = "123.3\t324234.5\t123124     \n124234\t32434\t434343\n12.3\t0.1"

        val result = TextToTableParser(testString).parsedTable
    }
}
package ru.nucodelabs.gem.view.tables

import org.junit.jupiter.api.Test

internal class TextToTableParserTest {
    @Test
    fun parse() {
        val testString = """
            123.3 324234.5 123124
            124234 32434 434343
        """.trimIndent()

        TextToTableParser(testString).parseTabulation(testString).also { println(it) }
    }
}
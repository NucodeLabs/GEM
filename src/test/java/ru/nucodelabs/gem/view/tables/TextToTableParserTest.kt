package ru.nucodelabs.gem.view.tables

import org.junit.jupiter.api.Test

internal class TextToTableParserTest {
    @Test
    fun parse() {
        val testString = "1.5\t0.5\t187.77\n" +
                "2\t0.5\t183.77\n" +
                "3\t0.5\t213.32\n" +
                "4\t0.5\t237.60\n" +
                "5\t0.5\t286.12\n" +
                "7\t0.5\t369.09\n" +
                "9\t0.5\t446.48\n" +
                "12\t0.5\t584.82\n" +
                "15\t0.5\t720.19\n" +
                "15\t3\t536.09\n" +
                "20\t0.5\t954.45\n" +
                "20\t3\t718.60\n" +
                "25\t3\t861.18\n" +
                "32\t3\t1131.99\n" +
                "40\t3\t1441.16\n" +
                "50\t3\t1682.52\n" +
                "65\t3\t2086.08\n" +
                "65\t12\t1639.99\n" +
                "80\t3\t2258.77\n" +
                "80\t12\t1789.32\n" +
                "100\t12\t1857.82\n" +
                "123\t12\t1843.84\n" +
                "150\t12\t1571.47\n" +
                "180\t12\t1296.25\n" +
                "180\t29\t1250.26\n" +
                "220\t12\t947.51\n" +
                "220\t29\t924.80\n" +
                "275\t29\t498.24\n" +
                "340\t29\t261.07\n" +
                "410\t29\t135.89\n" +
                "480\t29\t88.28"

        val textToTableParser = TextToTableParser(testString)
        val result = textToTableParser.parsedTable
    }
}
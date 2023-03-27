package ru.nucodelabs.gem.util.std

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AutoEqualsTest {
    class Pojo : AutoEquals() {
        init {
            includeInEquals(
                { name }
            )
        }

        var name: String = ""
        var age: Int = 0
    }

    @Test
    fun test() {
        val pojo1 = Pojo()
        val pojo2 = Pojo()

        assertTrue { pojo1 == pojo2 }
        assertTrue { pojo2 == pojo1 }

        pojo1.age = 10

        assertTrue { pojo1 == pojo2 }
        assertTrue { pojo2 == pojo1 }

        pojo2.name = "NAME"

        assertFalse { pojo1 == pojo2 }
        assertFalse { pojo2 == pojo1 }
    }
}
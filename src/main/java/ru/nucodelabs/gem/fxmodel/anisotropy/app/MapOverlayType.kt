package ru.nucodelabs.gem.fxmodel.anisotropy.app

enum class MapOverlayType {
    TRANSPARENCY {
        override fun toString(): String = "Полупрозрачность"
    },
    MULTIPLY {
        override fun toString(): String = "Умножение"
    },
    NONE {
        override fun toString(): String = "Не накладывать"
    },
}
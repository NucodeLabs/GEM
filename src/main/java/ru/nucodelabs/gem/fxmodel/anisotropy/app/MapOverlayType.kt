package ru.nucodelabs.gem.fxmodel.anisotropy.app

import javafx.scene.effect.BlendMode

enum class MapOverlayType(val fxMode: BlendMode?) {
    MULTIPLY(BlendMode.MULTIPLY) {
        override fun toString(): String = "Умножение"
    },
    SOFT_LIGHT(BlendMode.SOFT_LIGHT) {
        override fun toString(): String = "Мягкий свет"
    },
    COLOR_BURN(BlendMode.COLOR_BURN) {
        override fun toString(): String = "Color Burn"
    },
    OVERLAY(BlendMode.OVERLAY) {
        override fun toString(): String = "Перекрытие"
    },
    SCREEN(BlendMode.SCREEN) {
        override fun toString(): String = "Экран"
    },
    HARD_LIGHT(BlendMode.HARD_LIGHT) {
        override fun toString(): String = "Жесткий свет"
    },
    NONE(null) {
        override fun toString(): String = "Обычный"
    },
}
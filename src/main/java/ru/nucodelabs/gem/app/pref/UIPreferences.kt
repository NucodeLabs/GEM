@file:JvmName("UIPreferences")
package ru.nucodelabs.gem.app.pref

import ru.nucodelabs.kfx.pref.Preference

@JvmField
val VES_CURVES_LEGEND_VISIBLE = Preference("VES_CURVES_LEGEND", false)

@JvmField
val GRAPHS_TITLES = Preference("GRAPHS_TITLES", true)

@JvmField
val COLOR_MIN_VALUE = Preference("COLOR_MIN_VALUE", 1.0)

@JvmField
val COLOR_MAX_VALUE = Preference("COLOR_MAX_VALUE", 10_000.0)

@JvmField
val COLOR_SEGMENTS = Preference("COLOR_SEGMENTS", 15)

// MainSplitLayoutView

@JvmField
val VES_SECTION_SPLIT_DIV = Preference("VES_SECT_SPLIT_DIV", 0.5)

@JvmField
val SECTION_SPLIT_DIV = Preference("SECT_SPLIT_DIV", 0.5)

@JvmField
val VES_MISFIT_SPLIT_DIV = Preference("VES_MSF_SPLIT_DIV", 0.3)

@JvmField
val CURVES_TABLE_SPLIT_DIV = Preference("CURVES_TABLE_SPLIT_DIV", 0.6)
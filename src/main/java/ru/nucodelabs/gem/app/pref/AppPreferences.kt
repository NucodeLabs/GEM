package ru.nucodelabs.gem.app.pref

private val USER_HOME: String = System.getProperty("user.home")

@JvmField
val RECENT_FILES = Preference("RECENT_FILES", "")

@JvmField
val EXP_FILES_DIR = Preference("EXP_FC_INIT_DIR", USER_HOME)

@JvmField
val JSON_FILES_DIR = Preference("JSON_FC_INIT_DIR", USER_HOME)

@JvmField
val MOD_FILES_DIR = Preference("MOD_FC_INIT_DIR", USER_HOME)

@JvmField
val MAIN_WINDOW_X = Preference("WINDOW_X", .0)

@JvmField
val MAIN_WINDOW_Y = Preference("WINDOW_Y", .0)

@JvmField
val MAIN_WINDOW_W = Preference("WINDOW_W", 1280.0)

@JvmField
val MAIN_WINDOW_H = Preference("WINDOW_H", 720.0)


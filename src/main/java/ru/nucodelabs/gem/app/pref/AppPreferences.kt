package ru.nucodelabs.gem.app.pref

private val USER_HOME: String = System.getProperty("user.home")

@JvmField
val RECENT_FILES: Preference<String> = object : Preference<String>("RECENT_FILES", "") {}

@JvmField
val EXP_FILES_DIR: Preference<String> = object : Preference<String>("EXP_FC_INIT_DIR", USER_HOME) {}

@JvmField
val JSON_FILES_DIR = object : Preference<String>("JSON_FC_INIT_DIR", USER_HOME) {}

@JvmField
val MOD_FILES_DIR = object : Preference<String>("MOD_FC_INIT_DIR", USER_HOME) {}

@JvmField
val MAIN_WINDOW_X = object : Preference<Double>("WINDOW_X", .0) {}

@JvmField
val MAIN_WINDOW_Y = object : Preference<Double>("WINDOW_Y", .0) {}

@JvmField
val MAIN_WINDOW_W = object : Preference<Double>("WINDOW_W", 1280.0) {}

@JvmField
val MAIN_WINDOW_H = object : Preference<Double>("WINDOW_H", 720.0) {}


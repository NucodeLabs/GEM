package ru.nucodelabs.kfx.pref

/**
 * Data class for storing data to use with JDK Preferences API
 */
data class Preference<T>(val key: String, val def: T)
package ru.nucodelabs.gem.app.pref

data class Preference<T>(val key: String, val def: T)
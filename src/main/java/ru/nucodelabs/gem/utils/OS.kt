package ru.nucodelabs.gem.utils

import ru.nucodelabs.gem.utils.OS.isLinuxOrOther
import ru.nucodelabs.gem.utils.OS.isMacOS
import ru.nucodelabs.gem.utils.OS.isWindows

object OS {
    private val osNameNormalized = System.getProperty("os.name").lowercase()

    @JvmStatic
    val isMacOS = osNameNormalized.contains("mac")

    @JvmStatic
    val isWindows = osNameNormalized.contains("windows")

    @JvmStatic
    val isLinuxOrOther = !isMacOS && !isWindows
}

inline fun macOs(block: () -> Unit) {
    if (isMacOS) block()
}

inline fun linux(block: () -> Unit) {
    if (isLinuxOrOther) block()
}

inline fun windows(block: () -> Unit) {
    if (isWindows) block()
}
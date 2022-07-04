package ru.nucodelabs.gem.utils

object OS {
    private val osNameNormalized = System.getProperty("os.name").lowercase()

    @JvmStatic
    val isMacOS = osNameNormalized.contains("mac")

    @JvmStatic
    val isWindows = osNameNormalized.contains("windows")

    @JvmStatic
    val isLinuxOrOther = !isMacOS && !isWindows

    /**
     * `block` runs only on macOS
     */
    inline fun macOS(block: () -> Unit) {
        if (isMacOS) block()
    }

    /**
     * `block` runs only on Linux
     */
    inline fun linux(block: () -> Unit) {
        if (isLinuxOrOther) block()
    }

    /**
     * `block` runs only on Windows
     */
    inline fun windows(block: () -> Unit) {
        if (isWindows) block()
    }
}
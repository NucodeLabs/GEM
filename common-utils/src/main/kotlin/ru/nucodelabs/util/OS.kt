package ru.nucodelabs.util

/**
 * Utility object that tries to define current OS and offers some DSL syntax to run OS-specific blocks of code
 */
object OS {

    private val osName = System.getProperty("os.name").lowercase()
    val osType: OsType = when {
        setOf("mac os x", "osx", "darwin").any { osName.contains(it) } -> OsType.MAC_OS
        osName.contains("windows") -> OsType.WINDOWS
        osName.contains("linux") -> OsType.LINUX
        else -> OsType.OTHER
    }

    @JvmStatic
    val isMacOS = osType == OsType.MAC_OS

    @JvmStatic
    val isWindows = osType == OsType.MAC_OS

    @JvmStatic
    val isLinux = osType == OsType.LINUX

    val osNameClassifier = when (osType) {
        OsType.MAC_OS -> "macosx"
        OsType.WINDOWS -> "windows"
        OsType.LINUX -> "linux"
        OsType.OTHER -> NotImplementedError("Supported os types: ${OsType.entries}")
    }

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
        if (isLinux) block()
    }

    /**
     * `block` runs only on Windows
     */
    inline fun windows(block: () -> Unit) {
        if (isWindows) block()
    }
}

enum class OsType {
    MAC_OS, WINDOWS, LINUX, OTHER,
}
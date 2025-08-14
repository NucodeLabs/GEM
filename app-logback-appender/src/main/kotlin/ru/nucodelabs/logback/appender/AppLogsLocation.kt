package ru.nucodelabs.logback.appender

import ru.nucodelabs.util.OS
import ru.nucodelabs.util.OsType
import java.nio.file.Path
import kotlin.io.path.Path

const val DEFAULT_LOG_FILE = "application.log"

fun appLogsDir(appName: String): Path {
    return when (OS.osType) {
        OsType.MAC_OS -> Path("${System.getenv("HOME")}/Library/Logs/${appName}")
        OsType.WINDOWS -> Path("${System.getenv("USERPROFILE")}/AppData/Roaming/${appName}/logs")
        OsType.LINUX -> Path("${System.getenv("HOME")}/.config/${appName}/logs")
        OsType.OTHER -> throw NotImplementedError("Unsupported platform")
    }
}

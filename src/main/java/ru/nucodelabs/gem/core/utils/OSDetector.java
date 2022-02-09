package ru.nucodelabs.gem.core.utils;

import java.util.Locale;

public class OSDetector {
    private final boolean macOS;
    private final boolean windows;
    private final boolean linux;

    public OSDetector() {
        var osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        macOS = osName.contains("mac");
        windows = osName.contains("windows");
        linux = !macOS && !windows;
    }

    public boolean isMacOS() {
        return macOS;
    }

    public boolean isWindows() {
        return windows;
    }

    public boolean isLinux() {
        return linux;
    }
}

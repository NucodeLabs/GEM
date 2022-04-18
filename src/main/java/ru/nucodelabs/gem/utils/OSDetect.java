package ru.nucodelabs.gem.utils;

import java.util.Locale;

public class OSDetect {
    private final static boolean macOS;
    private final static boolean windows;
    private final static boolean linux;

    private OSDetect() {
    }

    static {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        macOS = osName.contains("mac");
        windows = osName.contains("windows");
        linux = !macOS && !windows;
    }

    public static boolean isMacOS() {
        return macOS;
    }

    public static boolean isWindows() {
        return windows;
    }

    public static boolean isLinux() {
        return linux;
    }
}

package ru.nucodelabs.gem.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArgNames {
    public static final String INITIAL = "initial";
    public static final String CSS = "CSS";
    public static final String CLR = "CLR";
    public static final String PRECISE = "Precise";
    public static final String SAVE = "SAVE";

    public static class View {
        public static final String MAIN_VIEW = "MainView";
        public static final String ANISOTROPY_MAIN_VIEW = "AnisotropyMainView";
    }

    public static class File {
        public static final String JSON = "JSON";
        public static final String MOD = "MOD";
        public static final String EXP = "EXP";
        public static final String PNG = "PNG";
    }
}

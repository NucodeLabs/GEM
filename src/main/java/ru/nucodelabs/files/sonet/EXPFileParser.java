package ru.nucodelabs.files.sonet;

import java.io.File;

public final class EXPFileParser {
    private final File expFile;

    public EXPFileParser(File expFile) {
        this.expFile = expFile;
    }

    public EXPFile parse() throws Exception {
        return SonetImportUtils.readEXP(expFile);
    }
}

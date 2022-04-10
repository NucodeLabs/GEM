package ru.nucodelabs.files.sonet;

import java.io.File;

public final class MODFileParser {
    private final File modFile;

    public MODFileParser(File modFile) {
        this.modFile = modFile;
    }

    public MODFile parse() throws Exception {
        return SonetImportUtils.readMOD(modFile);
    }
}

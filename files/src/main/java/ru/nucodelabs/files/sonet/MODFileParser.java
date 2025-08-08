package ru.nucodelabs.files.sonet;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;

public final class MODFileParser {
    private final File modFile;

    public MODFileParser(File modFile) {
        this.modFile = modFile;
    }

    public @NotNull MODFile parse() throws FileNotFoundException {
        return SonetImportUtils.readMOD(modFile);
    }
}

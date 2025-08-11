package ru.nucodelabs.files.sonet;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;

public final class EXPFileParser {
    private final File expFile;

    public EXPFileParser(File expFile) {
        this.expFile = expFile;
    }

    public @NotNull EXPFile parse() throws FileNotFoundException {
        return SonetImportUtils.readEXP(expFile);
    }
}

package ru.nucodelabs.files.sonet;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;

public final class STTFileParser {
    private final File sttFile;

    public STTFileParser(File sttFile) {
        this.sttFile = sttFile;
    }

    public @NotNull STTFile parse() throws FileNotFoundException {
        return SonetImportUtils.readSTT(sttFile);
    }
}

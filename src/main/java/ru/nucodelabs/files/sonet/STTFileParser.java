package ru.nucodelabs.files.sonet;

import java.io.File;

public final class STTFileParser {
    private final File sttFile;

    public STTFileParser(File sttFile) {
        this.sttFile = sttFile;
    }

    public STTFile parse() throws Exception {
        return SonetImportUtils.readSTT(sttFile);
    }
}

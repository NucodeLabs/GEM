package ru.nucodelabs.gem.app.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

class FileManagerImpl implements FileManager {

    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    public Section loadSectionFromJsonFile(File jsonFile) throws Exception {
        FileReader fileReader = new FileReader(jsonFile);
        Section section = objectMapper.readValue(fileReader, Section.class);
        fileReader.close();
        return section;
    }

    @Override
    public void saveSectionToJsonFile(File jsonFile, Section section) throws IOException {
        jsonFile.createNewFile();
        FileWriter fileWriter = new FileWriter(jsonFile);
        objectMapper.writeValue(fileWriter, section);
        fileWriter.close();
    }

    @Override
    public Picket loadPicketFromJsonFile(File jsonFile) throws Exception {
        return objectMapper.readValue(jsonFile, Picket.class);
    }

    @Override
    public void savePicketToJsonFile(File jsonFile, Picket picket) throws Exception {
        objectMapper.writeValue(jsonFile, picket);
    }
}

package ru.nucodelabs.gem.app.io;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ru.nucodelabs.data.ves.Picket;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class FileManagerImpl implements FileManager {

    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    public List<Picket> loadSectionFromJsonFile(File jsonFile) throws Exception {
        FileReader fileReader = new FileReader(jsonFile);
        TypeReference<ArrayList<Picket>> listTypeReference = new TypeReference<>() {};
        List<Picket> picketList = objectMapper.readValue(fileReader, listTypeReference);
        fileReader.close();
        return picketList;
    }

    @Override
    public void saveSectionToJsonFile(File jsonFile, List<Picket> section) throws IOException {
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

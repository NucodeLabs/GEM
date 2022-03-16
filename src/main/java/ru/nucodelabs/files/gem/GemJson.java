package ru.nucodelabs.files.gem;

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

public class GemJson {
    private final ObjectMapper objectMapper;

    public GemJson() {
        objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void writeData(Object data, File file) throws Exception {
        file.createNewFile();
        FileWriter fileWriter = new FileWriter(file);
        objectMapper.writeValue(fileWriter, data);
        fileWriter.close();
    }

    public List<Picket> readPicketList(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        TypeReference<ArrayList<Picket>> listTypeReference = new TypeReference<>() {
        };
        List<Picket> picketList = objectMapper.readValue(fileReader, listTypeReference);
        fileReader.close();
        return picketList;
    }
}

package ru.nucodelabs.files.gem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.model.SectionImpl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GemJson {
    private static final Gson gson;

    private GemJson() {
    }

    static {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public static void writeData(Object data, File file) throws IOException {
        file.createNewFile();
        FileWriter fileWriter = new FileWriter(file);
        gson.toJson(data, fileWriter);
        fileWriter.close();
    }

    public static Section readSection(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        Section section = gson.fromJson(fileReader, SectionImpl.class);
        fileReader.close();
        return section;
    }
}

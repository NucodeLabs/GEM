package ru.nucodelabs.gem.model;

import java.util.List;

public interface Sections extends Model {
    Section getSection(int sectionNumber);

    Section getLastSection();

    void addSection(Section section);

    void removeSection(int sectionNumber);

    int sectionsCnt();

    List<Section> getSections();
}

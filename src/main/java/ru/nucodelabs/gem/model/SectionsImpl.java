package ru.nucodelabs.gem.model;

import java.util.ArrayList;
import java.util.List;

public class SectionsImpl implements Sections {

    private final List<Section> sections;

    public SectionsImpl() {
        sections = new ArrayList<>();
    }

    @Override
    public Section getSection(int sectionNumber) {
        return sections.get(sectionNumber);
    }

    @Override
    public Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    @Override
    public void addSection(Section section) {
        sections.add(section);
    }

    @Override
    public void removeSection(int sectionNumber) {
        sections.remove(sectionNumber);
    }

    @Override
    public int sectionsCnt() {
        return sections.size();
    }

    @Override
    public List<Section> getSections() {
        return sections;
    }
}

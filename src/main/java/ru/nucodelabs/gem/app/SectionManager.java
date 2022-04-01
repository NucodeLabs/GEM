package ru.nucodelabs.gem.app;

import com.google.inject.name.Named;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;

import javax.inject.Inject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SectionManager {

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private Section section;

    @Inject
    public SectionManager(@Named("Initial") Section section) {
        this.section = section;
    }

    public Section getSnapshot() {
        return new Section(new ArrayList<>(section.pickets()));
    }

    public void setSection(Section section) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, "section", this.section, section);
        this.section = new Section(new ArrayList<>(section.pickets()));
        propertyChangeSupport.firePropertyChange(event);
    }

    public void updateModelData(int index, ModelData modelData) {
        Picket old = section.pickets().get(index);
        Picket picket = new Picket(old.name(), old.experimentalData(), modelData);
        section.pickets().set(index, picket);
        propertyChangeSupport.fireIndexedPropertyChange("picket", index, old, picket);
    }

    public void updateExperimentalData(int index, ExperimentalData experimentalData) {
        Picket old = section.pickets().get(index);
        Picket picket = new Picket(old.name(), experimentalData, old.modelData());
        section.pickets().set(index, picket);
        propertyChangeSupport.fireIndexedPropertyChange("picket", index, old, picket);
    }

    public void updateName(int index, String name) {
        Picket old = section.pickets().get(index);
        Picket picket = new Picket(name, old.experimentalData(), old.modelData());
        section.pickets().set(index, picket);
        propertyChangeSupport.fireIndexedPropertyChange("picket", index, old, picket);
    }

    public void updatePicket(int index, Picket picket) {
        Picket old = section.pickets().get(index);
        section.pickets().set(index, picket);
        propertyChangeSupport.fireIndexedPropertyChange("picket", index, old, picket);
    }

    public void add(Picket picket) {
        List<Picket> pickets = List.copyOf(section.pickets());
        section.pickets().add(picket);
        propertyChangeSupport.firePropertyChange("pickets", pickets, section.pickets());
    }

    public void swap(int index1, int index2) {
        List<Picket> pickets = List.copyOf(section.pickets());
        Collections.swap(section.pickets(), index1, index2);
        propertyChangeSupport.firePropertyChange("pickets", pickets, section.pickets());
    }

    public void remove(int index) {
        List<Picket> pickets = List.copyOf(section.pickets());
        section.pickets().remove(index);
        propertyChangeSupport.firePropertyChange("pickets", pickets, section.pickets());
    }

    public Picket get(int index) {
        return section.pickets().get(index);
    }

    public int size() {
        return section.pickets().size();
    }

    public void subscribe(PropertyChangeListener changeListener) {
        propertyChangeSupport.addPropertyChangeListener(changeListener);
    }
}

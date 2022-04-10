package ru.nucodelabs.gem.app.model;

import com.google.inject.name.Named;
import ru.nucodelabs.algorithms.inverse_solver.InverseSolver;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public class SectionManager extends SubmissionPublisher<Section> {

    private Section section;

    @Inject
    public SectionManager(@Named("Initial") Section section) {
        super(Runnable::run, Flow.defaultBufferSize()); // single threaded
        this.section = section;
        submit(section);
    }

    public Section getSnapshot() {
        return new Section(new ArrayList<>(section.pickets()));
    }

    public void setSection(Section section) {
        this.section = new Section(new ArrayList<>(section.pickets()));
        submit(section);
    }

    public void updateModelData(int index, ModelData modelData) {
        Picket old = section.pickets().get(index);
        Picket picket = new Picket(old.name(), old.experimentalData(), modelData);
        section.pickets().set(index, picket);
        submit(section);
    }

    public void updateExperimentalData(int index, ExperimentalData experimentalData) {
        Picket old = section.pickets().get(index);
        Picket picket = new Picket(old.name(), experimentalData, old.modelData());
        section.pickets().set(index, picket);
        submit(section);
    }

    public void updateName(int index, String name) {
        Picket old = section.pickets().get(index);
        Picket picket = new Picket(name, old.experimentalData(), old.modelData());
        section.pickets().set(index, picket);
        submit(section);
    }

    public void updatePicket(int index, Picket picket) {
        section.pickets().set(index, picket);
        submit(section);
    }

    public void add(Picket picket) {
        section.pickets().add(picket);
        submit(section);
    }

    public void swap(int index1, int index2) {
        Collections.swap(section.pickets(), index1, index2);
        submit(section);
    }

    public void remove(int index) {
        section.pickets().remove(index);
        submit(section);
    }

    public Picket get(int index) {
        return section.pickets().get(index);
    }

    public int size() {
        return section.pickets().size();
    }

    public void inverseSolve(int index) {
        InverseSolver inverseSolver = new InverseSolver(get(index));
        updateModelData(index, inverseSolver.getOptimizedModelData());
    }
}

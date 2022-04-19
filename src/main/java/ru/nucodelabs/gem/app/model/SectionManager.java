package ru.nucodelabs.gem.app.model;

import ru.nucodelabs.algorithms.inverse_solver.InverseSolver;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelLayer;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.app.snapshot.Snapshot;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public class SectionManager extends SubmissionPublisher<Section> implements Snapshot.Originator<Section> {

    private Section section = Section.create(new ArrayList<>());

    @Inject
    public SectionManager() {
        super(Runnable::run, Flow.defaultBufferSize()); // single threaded
        submit(section);
    }

    @Override
    public Snapshot<Section> getSnapshot() {
        return Snapshot.create(Section.create(new ArrayList<>(section.getPickets())));
    }

    @Override
    public void restoreFromSnapshot(Snapshot<Section> snapshot) {
        this.section = Section.create(new ArrayList<>(snapshot.get().getPickets()));
        submit(section);
    }

    public void updateModelData(int index, List<ModelLayer> modelData) {
        Picket old = section.getPickets().get(index);
        Picket picket = Picket.create(
                old.getName(), old.getExperimentalData(), modelData, old.getX(), old.getZ());
        section.getPickets().set(index, picket);
        submit(section);
    }

    public void updateExperimentalData(int index, List<ExperimentalData> experimentalData) {
        Picket old = section.getPickets().get(index);
        Picket picket = Picket.create(
                old.getName(), experimentalData, old.getModelData(), old.getX(), old.getZ());
        section.getPickets().set(index, picket);
        submit(section);
    }

    public void updateName(int index, String name) {
        Picket old = section.getPickets().get(index);
        Picket picket = Picket.create(
                name, old.getExperimentalData(), old.getModelData(), old.getX(), old.getZ());
        section.getPickets().set(index, picket);
        submit(section);
    }

    public void updatePicket(int index, Picket picket) {
        section.getPickets().set(index, picket);
        submit(section);
    }

    public void add(Picket picket) {
        section.getPickets().add(picket);
        submit(section);
    }

    public void swap(int index1, int index2) {
        Collections.swap(section.getPickets(), index1, index2);
        submit(section);
    }

    public void remove(int index) {
        section.getPickets().remove(index);
        submit(section);
    }

    public void updateX(int index, double x) {
        Picket old = section.getPickets().get(index);
        Picket newP = Picket.create(old.getName(), old.getExperimentalData(), old.getModelData(), x, old.getZ());
        section.getPickets().set(index, newP);
        submit(section);
    }

    public void updateZ(int index, double z) {
        Picket old = section.getPickets().get(index);
        Picket newP = Picket.create(old.getName(), old.getExperimentalData(), old.getModelData(), old.getX(), z);
        section.getPickets().set(index, newP);
        submit(section);
    }

    public Picket get(int index) {
        return section.getPickets().get(index);
    }

    public int size() {
        return section.getPickets().size();
    }

    public void inverseSolve(int index) {
        InverseSolver inverseSolver = new InverseSolver(get(index));
        updateModelData(index, inverseSolver.getOptimizedModelData());
    }
}

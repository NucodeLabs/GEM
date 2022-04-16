package ru.nucodelabs.gem.app.model;

import com.google.inject.name.Named;
import ru.nucodelabs.algorithms.inverse_solver.InverseSolver;
import ru.nucodelabs.data.ves.ExperimentalMeasurement;
import ru.nucodelabs.data.ves.ModelLayer;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.app.snapshot.Snapshot;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.SubmissionPublisher;

public class SectionManager extends SubmissionPublisher<Section> implements Snapshot.Originator<ru.nucodelabs.data.ves.Section> {

    private Section section;

    @Inject
    public SectionManager(@Named("Initial") Section section) {
//        super(Runnable::run, Flow.defaultBufferSize()); // single threaded
        this.section = section;
        submit(section);
    }

    @Override
    public synchronized Snapshot<Section> getSnapshot() {
        return Snapshot.create(Section.create(new ArrayList<>(section.getPickets())));
    }

    @Override
    public synchronized void restoreFromSnapshot(Snapshot<Section> snapshot) {
        this.section = Section.create(new ArrayList<>(snapshot.get().getPickets()));
        submit(section);
    }

    public synchronized void updateModelData(int index, List<ModelLayer> modelData) {
        Picket old = section.getPickets().get(index);
        Picket picket = Picket.create(old.getName(), old.getExperimentalData(), modelData);
        section.getPickets().set(index, picket);
        submit(section);
    }

    public synchronized void updateExperimentalData(int index, List<ExperimentalMeasurement> experimentalData) {
        Picket old = section.getPickets().get(index);
        Picket picket = Picket.create(old.getName(), experimentalData, old.getModelData());
        section.getPickets().set(index, picket);
        submit(section);
    }

    public synchronized void updateName(int index, String name) {
        Picket old = section.getPickets().get(index);
        Picket picket = Picket.create(name, old.getExperimentalData(), old.getModelData());
        section.getPickets().set(index, picket);
        submit(section);
    }

    public synchronized void updatePicket(int index, Picket picket) {
        section.getPickets().set(index, picket);
        submit(section);
    }

    public synchronized void add(Picket picket) {
        section.getPickets().add(picket);
        submit(section);
    }

    public synchronized void swap(int index1, int index2) {
        Collections.swap(section.getPickets(), index1, index2);
        submit(section);
    }

    public synchronized void remove(int index) {
        section.getPickets().remove(index);
        submit(section);
    }

    public synchronized Picket get(int index) {
        return section.getPickets().get(index);
    }

    public synchronized int size() {
        return section.getPickets().size();
    }

    public synchronized void inverseSolve(int index) {
        InverseSolver inverseSolver = new InverseSolver(get(index));
        updateModelData(index, inverseSolver.getOptimizedModelData());
    }

    public synchronized void forceSubmitUpdate() {
        submit(section);
    }
}

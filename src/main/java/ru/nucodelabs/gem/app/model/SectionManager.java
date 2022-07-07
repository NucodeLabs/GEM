package ru.nucodelabs.gem.app.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.nucodelabs.algorithms.forward_solver.ForwardSolverKt;
import ru.nucodelabs.algorithms.inverse_solver.InverseSolver;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.app.snapshot.Snapshot;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public class SectionManager implements Snapshot.Originator<Section> {

    private final SubmissionPublisher<Section> sectionObservable
            = new SubmissionPublisher<>(Runnable::run, Flow.defaultBufferSize());

    private final MutableSection mutableSection = new MutableSection();

    @Inject
    public SectionManager() {
        sectionObservable.submit(mutableSection.toImmutable());
    }

    @Override
    public Snapshot<Section> snapshot() {
        return Snapshot.of(mutableSection.toImmutable());
    }

    @Override
    public void restoreFromSnapshot(Snapshot<Section> snapshot) {
        mutableSection.getPickets().clear();
        mutableSection.getPickets().addAll(snapshot.getValue().getPickets());
        sectionObservable.submit(mutableSection.toImmutable());
    }

    public boolean update(Picket picket) {
        var picketToUpdate = getById(picket.getId());
        if (picketToUpdate.isPresent()) {
            int index = mutableSection.getPickets().indexOf(picketToUpdate.get());
            mutableSection.getPickets().set(index, picket);
            sectionObservable.submit(mutableSection.toImmutable());
            return true;
        } else {
            return false;
        }
    }

    public void add(Picket picket) {
        mutableSection.getPickets().add(picket);
        sectionObservable.submit(mutableSection.toImmutable());
    }

    public void swap(int index1, int index2) {
        Collections.swap(mutableSection.getPickets(), index1, index2);
        sectionObservable.submit(mutableSection.toImmutable());
    }

    public void remove(int index) {
        mutableSection.getPickets().remove(index);
        sectionObservable.submit(mutableSection.toImmutable());
    }

    public Optional<Picket> getById(UUID uuid) {
        return mutableSection.getPickets().stream().filter(picket -> picket.getId().equals(uuid)).findFirst();
    }

    public Picket getByIndex(int index) {
        return mutableSection.getPickets().get(index);
    }

    public Optional<Integer> indexById(UUID uuid) {
        var picket = getById(uuid);
        return picket.map(value -> mutableSection.getPickets().indexOf(value));
    }

    public int size() {
        return mutableSection.getPickets().size();
    }

    public void inverseSolve(Picket picket) {
        var id = picket.getId();
        var picketToSolve = getById(id);
        if (picketToSolve.isPresent()) {
            InverseSolver inverseSolver = new InverseSolver(ForwardSolverKt.ForwardSolver());
            update(picketToSolve.get().withModelData(inverseSolver.getOptimizedModelData(picketToSolve.get())));
        }
    }

    public Flow.Publisher<Section> getSectionObservable() {
        return sectionObservable;
    }

    private static class MutableSection implements Section {

        private final List<Picket> pickets = new ArrayList<>();

        @Override
        public List<@Valid @NotNull Picket> getPickets() {
            return pickets;
        }

        public Section toImmutable() {
            return Section.create(getPickets());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Section section) {
                return section.getPickets().equals(this.getPickets());
            } else {
                return false;
            }
        }
    }
}

package ru.nucodelabs.gem.app.snapshot;

public interface Snapshot<T> {

    static <T> Snapshot<T> of(T value) {
        return new SnapshotImpl<>(value);
    }

    T value();

    interface Originator<T> {
        Snapshot<T> getSnapshot();

        void restoreFromSnapshot(Snapshot<T> snapshot);
    }
}

package ru.nucodelabs.gem.app.snapshot;

public interface Snapshot<T> {

    static <T> Snapshot<T> create(T data) {
        return new SnapshotImpl<>(data);
    }

    T get();

    interface Originator<T> {
        Snapshot<T> getSnapshot();

        void restoreFromSnapshot(Snapshot<T> snapshot);
    }
}

package ru.nucodelabs.gem.app.snapshot;

import java.util.Objects;

public interface Snapshot<T> {

    static <T> Snapshot<T> of(T value) {
        return new SnapshotImpl<>(
                Objects.requireNonNull(value));
    }

    T value();

    interface Originator<T> {
        Snapshot<T> getSnapshot();

        void restoreFromSnapshot(Snapshot<T> snapshot);
    }
}

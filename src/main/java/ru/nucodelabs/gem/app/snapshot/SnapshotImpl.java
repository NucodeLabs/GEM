package ru.nucodelabs.gem.app.snapshot;

record SnapshotImpl<T>(
        T data
) implements Snapshot<T> {
    @Override
    public T value() {
        return data();
    }
}

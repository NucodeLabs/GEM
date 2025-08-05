package ru.nucodelabs.kfx.snapshot

interface Snapshot<T> {
    val value: T

    interface Originator<T> {
        fun snapshot(): Snapshot<T>
        fun restoreFromSnapshot(snapshot: Snapshot<T>)
    }

    companion object Factory {
        @JvmStatic
        fun <T> of(value: T): Snapshot<T> {
            return SnapshotImpl(value)
        }
    }

    private data class SnapshotImpl<T>(override val value: T) : Snapshot<T>
}

fun <T> snapshotOf(value: T) = Snapshot.of(value)
package ru.nucodelabs.gem.app.snapshot

import ru.nucodelabs.gem.app.snapshot.Snapshot.Originator
import javax.inject.Inject

class HistoryManager<T> @Inject constructor(private val originator: Originator<T>) {
    private var history: MutableList<Snapshot<T>> = mutableListOf()
    private var position = 0

    /**
     * Makes a snapshot of originator
     */
    fun snapshot() {
        val snapshot = originator.snapshot()
        if (history.isEmpty() || history[position] != snapshot) {
            if (position < history.lastIndex) {
                history = history.subList(0, position + 1)
            }
            history += snapshot
            position = history.lastIndex
        }
    }

    /**
     * Makes a snapshot after executing block
     */
    fun snapshotAfter(block: () -> Unit) {
        block()
        snapshot()
    }

    /**
     * Restores originator from next snapshot in history if it exists else do nothing
     */
    fun redo() = snapshotToRedo()?.let { originator.restoreFromSnapshot(it) }

    /**
     * Restore originator from previous snapshot in history if it exists else do nothing
     */
    fun undo() = snapshotToUndo()?.let { originator.restoreFromSnapshot(it) }

    /**
     * Returns previous snapshot if it exists and change position
     */
    private fun snapshotToUndo(): Snapshot<T>? {
        if (history.isEmpty() || position == 0) {
            return null
        }
        position--
        return history[position]
    }

    /**
     * Returns next snapshot if it exists and changes position
     */
    private fun snapshotToRedo(): Snapshot<T>? {
        if (history.isEmpty() || position == history.lastIndex) {
            return null
        }
        position++
        return history[position]
    }

    /**
     * Reset
     */
    fun clear() {
        history.clear()
        position = 0
    }
}
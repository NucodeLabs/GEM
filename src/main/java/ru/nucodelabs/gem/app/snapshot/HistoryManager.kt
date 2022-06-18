package ru.nucodelabs.gem.app.snapshot

import ru.nucodelabs.gem.app.snapshot.Snapshot.Originator
import javax.inject.Inject

class HistoryManager<T> @Inject constructor(private val originator: Originator<T>) {
    private var history: MutableList<Snapshot<T>> = mutableListOf()
    private var position = 0

    fun snapshot() {
        val snapshot = originator.snapshot()
        if (history.isEmpty() || history[position] != snapshot) {
            if (position < history.lastIndex) {
                history = history.subList(0, 0.coerceAtLeast(position + 1))
            }
            history += snapshot
            position = history.lastIndex
        }
    }

    // TODO: fun snapshot(() -> Unit = {}) = ...
    // TODO: () -> Unit instead of Runnable
    fun snapshotAfter(block: Runnable) {
        block.run()
        snapshot()
    }

    fun redo() = snapshotToRedo()?.let { originator.restoreFromSnapshot(it) }

    fun undo() = snapshotToUndo()?.let { originator.restoreFromSnapshot(it) }

    private fun snapshotToUndo(): Snapshot<T>? {
        if (position == 0 || history.isEmpty()) {
            return null
        }
        position = 0.coerceAtLeast(position - 1)
        return history[position]
    }

    private fun snapshotToRedo(): Snapshot<T>? {
        if (position == history.lastIndex || history.isEmpty()) {
            return null
        }
        position = (history.size - 1).coerceAtMost(position + 1)
        return history[position]
    }

    fun clear() {
        history.clear()
        position = 0
    }
}
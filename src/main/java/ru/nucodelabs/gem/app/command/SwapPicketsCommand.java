package ru.nucodelabs.gem.app.command;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import ru.nucodelabs.data.ves.Picket;

import java.util.Collections;
import java.util.List;

public class SwapPicketsCommand extends AbstractCommand {

    private final int index1;
    private final int index2;

    @AssistedInject
    public SwapPicketsCommand(
            List<Picket> currentState,
            @Assisted("1") int index1,
            @Assisted("2") int index2) {
        super(currentState);
        this.index1 = index1;
        this.index2 = index2;
    }

    @Override
    public boolean execute() {
        Collections.swap(currentState, index1, index2);
        return true;
    }

    public interface Factory {
        SwapPicketsCommand create(@Assisted("1") int index1, @Assisted("2") int index2);
    }
}

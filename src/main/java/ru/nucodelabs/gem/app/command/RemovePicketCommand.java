package ru.nucodelabs.gem.app.command;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import ru.nucodelabs.data.ves.Picket;

import java.util.List;

public class RemovePicketCommand extends AbstractCommand {

    private final int picketIndex;

    @AssistedInject
    public RemovePicketCommand(
            List<Picket> currentState,
            @Assisted int picketIndex) {
        super(currentState);
        this.picketIndex = picketIndex;
    }

    @Override
    public boolean execute() {
        currentState.remove(picketIndex);
        return true;
    }

    public interface Factory {
        RemovePicketCommand create(int picketIndex);
    }
}

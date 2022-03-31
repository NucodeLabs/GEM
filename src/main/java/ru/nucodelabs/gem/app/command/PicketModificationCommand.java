package ru.nucodelabs.gem.app.command;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.app.annotation.State;

import java.util.List;

public class PicketModificationCommand extends AbstractCommand {

    private final int picketIndex;
    private final Picket newPicketValue;

    @AssistedInject
    public PicketModificationCommand(
            int picketIndex,
            @State List<Picket> state,
            @Assisted Picket newPicketValue) {
        super(state);
        this.picketIndex = picketIndex;
        this.newPicketValue = newPicketValue;
    }

    @Override
    public boolean execute() {
        state.set(picketIndex, newPicketValue);
        return true;
    }

    public interface Factory {
        PicketModificationCommand create(Picket newPicketValue);
    }
}

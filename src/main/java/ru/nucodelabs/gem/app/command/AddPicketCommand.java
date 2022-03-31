package ru.nucodelabs.gem.app.command;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import ru.nucodelabs.data.ves.Picket;

import java.util.List;

public class AddPicketCommand extends AbstractCommand {

    private final Picket newPicket;

    @AssistedInject
    public AddPicketCommand(
            List<Picket> state,
            @Assisted Picket newPicket) {
        super(state);
        this.newPicket = newPicket;
    }

    @Override
    public boolean execute() {
        state.add(newPicket);
        return true;
    }

    public interface Factory {
        AddPicketCommand create(Picket newPicket);
    }
}

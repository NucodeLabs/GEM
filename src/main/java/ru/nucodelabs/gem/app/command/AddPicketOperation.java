package ru.nucodelabs.gem.app.command;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.app.annotation.Subject;

import java.util.List;

public class AddPicketOperation extends AbstractOperation {

    private final Picket newPicket;

    @AssistedInject
    public AddPicketOperation(
            @Subject List<Picket> state,
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
        AddPicketOperation create(Picket newPicket);
    }
}

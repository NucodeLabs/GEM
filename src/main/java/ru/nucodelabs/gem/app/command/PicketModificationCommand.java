package ru.nucodelabs.gem.app.command;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import javafx.collections.ObservableList;
import ru.nucodelabs.data.ves.Picket;

public class PicketModificationCommand extends AbstractCommand {

    private final int picketIndex;
    private final Picket newPicketValue;

    @AssistedInject
    public PicketModificationCommand(
            int picketIndex,
            ObservableList<Picket> picketObservableList,
            @Assisted Picket newPicketValue) {
        super(picketObservableList);
        this.picketIndex = picketIndex;
        this.newPicketValue = newPicketValue;
    }

    @Override
    public boolean execute() {
        currentState.set(picketIndex, newPicketValue);
        return true;
    }

    public interface Factory {
        PicketModificationCommand create(Picket newPicketValue);
    }
}

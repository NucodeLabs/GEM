package ru.nucodelabs.gem.app.command;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import javafx.beans.property.ObjectProperty;
import ru.nucodelabs.data.ves.Picket;

public class PicketModificationCommand extends AbstractPicketCommand {

    private final Picket newPicketValue;

    @AssistedInject
    public PicketModificationCommand(ObjectProperty<Picket> picket, @Assisted Picket newPicketValue) {
        super(picket);
        this.newPicketValue = newPicketValue;
    }

    @Override
    public boolean execute() {
        picket.set(newPicketValue);
        return true;
    }

    public interface Factory {
        PicketModificationCommand create(Picket newPicketValue);
    }
}

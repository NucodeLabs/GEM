package ru.nucodelabs.gem.app.command;

import javafx.beans.property.ObjectProperty;
import ru.nucodelabs.data.ves.Picket;

public abstract class AbstractPicketCommand implements Command {

    protected final ObjectProperty<Picket> picket;
    private final Picket backup;

    public AbstractPicketCommand(ObjectProperty<Picket> picket) {
        this.picket = picket;
        backup = picket.get();
    }

    @Override
    public void undo() {
        picket.set(backup);
    }
}

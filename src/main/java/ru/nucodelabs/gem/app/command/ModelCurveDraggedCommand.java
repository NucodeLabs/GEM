package ru.nucodelabs.gem.app.command;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import javafx.beans.property.ObjectProperty;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;

public class ModelCurveDraggedCommand implements Command {

    private final ObjectProperty<Picket> picket;
    private final Picket previousState;
    private final ModelData modelData;

    @AssistedInject
    public ModelCurveDraggedCommand(
            ObjectProperty<Picket> picket,
            @Assisted Picket previousState,
            @Assisted ModelData modelData) {
        this.picket = picket;
        this.previousState = previousState;
        this.modelData = modelData;
    }

    @Override
    public void undo() {
        picket.set(previousState);
    }

    @Override
    public boolean execute() {
        picket.set(
                new Picket(
                        picket.get().name(),
                        picket.get().experimentalData(),
                        modelData
                )
        );

        return true;
    }

    public interface Factory {
        ModelCurveDraggedCommand create(Picket previousState, ModelData modelData);
    }
}

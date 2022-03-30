package ru.nucodelabs.gem.app.command;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;

import java.util.List;

public class ModelCurveDraggedCommand implements Command {

    private final int picketIndex;
    private final List<Picket> currentState;
    private final List<Picket> beforeDragState;
    private final ModelData modelData;

    @AssistedInject
    public ModelCurveDraggedCommand(
            int picketIndex,
            List<Picket> currentState,
            @Assisted List<Picket> beforeDragState,
            @Assisted ModelData modelData) {
        this.picketIndex = picketIndex;
        this.currentState = currentState;
        this.beforeDragState = List.copyOf(beforeDragState);
        this.modelData = modelData;
    }

    @Override
    public void undo() {
        currentState.clear();
        currentState.addAll(beforeDragState);
    }

    @Override
    public boolean execute() {
        Picket picket = currentState.get(picketIndex);
        currentState.set(picketIndex,
                new Picket(
                        picket.name(),
                        picket.experimentalData(),
                        modelData
                )
        );

        return true;
    }

    public interface Factory {
        ModelCurveDraggedCommand create(List<Picket> beforeDragState, ModelData afterDragModelData);
    }
}

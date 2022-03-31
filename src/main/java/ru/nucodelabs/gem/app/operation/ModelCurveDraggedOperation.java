package ru.nucodelabs.gem.app.operation;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.app.annotation.Subject;

import java.util.List;

public class ModelCurveDraggedOperation implements Operation {

    private final int picketIndex;
    private final List<Picket> currentState;
    private final List<Picket> beforeDragState;
    private final ModelData modelData;

    @AssistedInject
    public ModelCurveDraggedOperation(
            int picketIndex,
            @Subject List<Picket> state,
            @Assisted List<Picket> beforeDragState,
            @Assisted ModelData modelData) {
        this.picketIndex = picketIndex;
        this.currentState = state;
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
        ModelCurveDraggedOperation create(List<Picket> beforeDragState, ModelData afterDragModelData);
    }
}

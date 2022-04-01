package ru.nucodelabs.gem.app.operation;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.app.annotation.Subject;

import java.util.List;

public class ModelCurveDraggedOperation implements Operation {

    private final int picketIndex;
    private final List<Picket> subject;
    private final List<Picket> beforeDragSnapshot;
    private final ModelData modelData;

    @AssistedInject
    public ModelCurveDraggedOperation(
            int picketIndex,
            @Subject List<Picket> subject,
            @Assisted List<Picket> beforeDragSnapshot,
            @Assisted ModelData modelData) {
        this.picketIndex = picketIndex;
        this.subject = subject;
        this.beforeDragSnapshot = List.copyOf(beforeDragSnapshot);
        this.modelData = modelData;
    }

    @Override
    public void restoreSubjectFromSnapshot() {
        subject.clear();
        subject.addAll(beforeDragSnapshot);
    }

    @Override
    public boolean execute() {
        Picket picket = subject.get(picketIndex);
        subject.set(picketIndex,
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

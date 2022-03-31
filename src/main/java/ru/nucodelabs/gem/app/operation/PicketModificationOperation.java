package ru.nucodelabs.gem.app.operation;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.app.annotation.Subject;

import java.util.List;

public class PicketModificationOperation extends AbstractOperation {

    private final int picketIndex;
    private final Picket newPicketValue;

    @AssistedInject
    public PicketModificationOperation(
            int picketIndex,
            @Subject List<Picket> state,
            @Assisted Picket newPicketValue) {
        super(state);
        this.picketIndex = picketIndex;
        this.newPicketValue = newPicketValue;
    }

    @AssistedInject
    public PicketModificationOperation(
            int picketIndex,
            @Subject List<Picket> state,
            @Assisted ModelData newModelDataValue) {
        this(picketIndex, state,
                new Picket(
                        state.get(picketIndex).name(),
                        state.get(picketIndex).experimentalData(),
                        newModelDataValue
                ));
    }

    @AssistedInject
    public PicketModificationOperation(
            int picketIndex,
            @Subject List<Picket> state,
            @Assisted ExperimentalData newExperimentalDataValue) {
        this(picketIndex, state,
                new Picket(
                        state.get(picketIndex).name(),
                        newExperimentalDataValue,
                        state.get(picketIndex).modelData()
                ));
    }

    @AssistedInject
    public PicketModificationOperation(
            int picketIndex,
            @Subject List<Picket> state,
            @Assisted String newName) {
        this(picketIndex, state,
                new Picket(
                        newName,
                        state.get(picketIndex).experimentalData(),
                        state.get(picketIndex).modelData()
                ));
    }

    @Override
    public boolean execute() {
        state.set(picketIndex, newPicketValue);
        return true;
    }

    public interface Factory {
        PicketModificationOperation create(Picket newPicketValue);

        PicketModificationOperation create(String newName);

        PicketModificationOperation create(ModelData newModelDataValue);

        PicketModificationOperation create(ExperimentalData newExperimentalDataValue);
    }
}

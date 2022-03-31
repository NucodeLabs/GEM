package ru.nucodelabs.gem.app.command;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.app.annotation.State;

import java.util.List;

public class PicketModificationCommand extends AbstractCommand {

    private final int picketIndex;
    private final Picket newPicketValue;

    @AssistedInject
    public PicketModificationCommand(
            int picketIndex,
            @State List<Picket> state,
            @Assisted Picket newPicketValue) {
        super(state);
        this.picketIndex = picketIndex;
        this.newPicketValue = newPicketValue;
    }

    @AssistedInject
    public PicketModificationCommand(
            int picketIndex,
            @State List<Picket> state,
            @Assisted ModelData newModelDataValue) {
        this(picketIndex, state,
                new Picket(
                        state.get(picketIndex).name(),
                        state.get(picketIndex).experimentalData(),
                        newModelDataValue
                ));
    }

    @AssistedInject
    public PicketModificationCommand(
            int picketIndex,
            @State List<Picket> state,
            @Assisted ExperimentalData newExperimentalDataValue) {
        this(picketIndex, state,
                new Picket(
                        state.get(picketIndex).name(),
                        newExperimentalDataValue,
                        state.get(picketIndex).modelData()
                ));
    }

    @AssistedInject
    public PicketModificationCommand(
            int picketIndex,
            @State List<Picket> state,
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
        PicketModificationCommand create(Picket newPicketValue);

        PicketModificationCommand create(String newName);

        PicketModificationCommand create(ModelData newModelDataValue);

        PicketModificationCommand create(ExperimentalData newExperimentalDataValue);
    }
}

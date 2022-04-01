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
            @Subject List<Picket> subject,
            @Assisted Picket newPicketValue) {
        super(subject);
        this.picketIndex = picketIndex;
        this.newPicketValue = newPicketValue;
    }

    @AssistedInject
    public PicketModificationOperation(
            int picketIndex,
            @Subject List<Picket> subject,
            @Assisted ModelData newModelDataValue) {
        this(picketIndex, subject,
                new Picket(
                        subject.get(picketIndex).name(),
                        subject.get(picketIndex).experimentalData(),
                        newModelDataValue
                ));
    }

    @AssistedInject
    public PicketModificationOperation(
            int picketIndex,
            @Subject List<Picket> subject,
            @Assisted ExperimentalData newExperimentalDataValue) {
        this(picketIndex, subject,
                new Picket(
                        subject.get(picketIndex).name(),
                        newExperimentalDataValue,
                        subject.get(picketIndex).modelData()
                ));
    }

    @AssistedInject
    public PicketModificationOperation(
            int picketIndex,
            @Subject List<Picket> subject,
            @Assisted String newName) {
        this(picketIndex, subject,
                new Picket(
                        newName,
                        subject.get(picketIndex).experimentalData(),
                        subject.get(picketIndex).modelData()
                ));
    }

    @Override
    public boolean execute() {
        subject.set(picketIndex, newPicketValue);
        return true;
    }

    public interface Factory {
        PicketModificationOperation create(Picket newPicketValue);

        PicketModificationOperation create(String newName);

        PicketModificationOperation create(ModelData newModelDataValue);

        PicketModificationOperation create(ExperimentalData newExperimentalDataValue);
    }
}

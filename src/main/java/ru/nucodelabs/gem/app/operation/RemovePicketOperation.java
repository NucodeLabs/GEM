package ru.nucodelabs.gem.app.operation;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.app.annotation.Subject;

import java.util.List;

public class RemovePicketOperation extends AbstractOperation {

    private final int picketIndex;

    @AssistedInject
    public RemovePicketOperation(
            @Subject List<Picket> subject,
            @Assisted int picketIndex) {
        super(subject);
        this.picketIndex = picketIndex;
    }

    @Override
    public boolean execute() {
        subject.remove(picketIndex);
        return true;
    }

    public interface Factory {
        RemovePicketOperation create(int picketIndex);
    }
}

package ru.nucodelabs.data.ves;

import java.io.Serializable;
import java.util.List;

public record SectionImpl(
        List<Picket> pickets
) implements Serializable, Section {
    @Override
    public List<Picket> getPickets() {
        return pickets();
    }
}

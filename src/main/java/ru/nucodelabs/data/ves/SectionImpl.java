package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

record SectionImpl(
        List<Picket> pickets
) implements Serializable, Section {

    @JsonCreator
    static SectionImpl create(
            @JsonProperty("pickets") List<Picket> pickets
    ) {
        return new SectionImpl(List.copyOf(pickets));
    }

    @Override
    public List<Picket> getPickets() {
        return pickets();
    }
}

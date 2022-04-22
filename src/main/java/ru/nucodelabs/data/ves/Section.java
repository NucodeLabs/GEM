package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.DEDUCTION,
        defaultImpl = SectionImpl.class
)
public interface Section extends Serializable {

    static Section create(
            List<Picket> pickets
    ) {
        return new SectionImpl(List.copyOf(pickets));
    }

    Section DEFAULT = Section.create(Collections.emptyList());

    @NotNull List<@Valid @NotNull Picket> getPickets();
}

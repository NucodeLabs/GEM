package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.DEDUCTION,
        defaultImpl = SectionImpl.class
)
public interface Section {

    static Section create(
            List<Picket> pickets
    ) {
        return new SectionImpl(pickets);
    }

    @NotNull @Valid List<Picket> getPickets();
}

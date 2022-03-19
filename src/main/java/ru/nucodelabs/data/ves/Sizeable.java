package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static ru.nucodelabs.data.ves.Sizes.isEqualSizes;
import static ru.nucodelabs.data.ves.Sizes.minSize;

public interface Sizeable {
    @JsonIgnore
    default int getSize() {
        return minSize(this, true);
    }

    @JsonIgnore
    default boolean isUnsafe() {
        return !isEqualSizes(this, true);
    }
}

package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static ru.nucodelabs.data.ves.SizesReflectionUtil.isEqualSizes;
import static ru.nucodelabs.data.ves.SizesReflectionUtil.minSize;

interface Sizeable {
    @JsonIgnore
    default int size() {
        return minSize(this, true);
    }

    @JsonIgnore
    default boolean isUnsafe() {
        return !isEqualSizes(this, true);
    }
}

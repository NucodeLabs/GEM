package ru.nucodelabs.data.ves;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Враппер для списка пикетов, используемый преимущественно для удобной валидации
 *
 * @param pickets
 */
public record Section(@NotNull @Valid List<Picket> pickets) {
}

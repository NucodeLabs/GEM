package ru.nucodelabs.gem.file.dto.anisotropy

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SignalDto(
    var ab2: Double,
    var mn2: Double,
    var amperage: Double?,
    var voltage: Double?,
    var resistivityApparent: Double?,
    var errorResistivityApparent: Double?,
    var isHidden: Boolean?
)

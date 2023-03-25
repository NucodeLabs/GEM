package ru.nucodelabs.gem.file.dto.project

import com.fasterxml.jackson.annotation.JsonTypeInfo

data class ProjectDto<T>(
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
    )
    var data: T
)

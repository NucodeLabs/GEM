package ru.nucodelabs.gem.file.dto.project;

import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import ru.nucodelabs.gem.app.project.Project;

@MapperConfig
public interface AbstractProjectDtoMapper<M, D> {
    @Mapping(target = "copy", ignore = true)
    ProjectDto<D> toDto(Project<M> project);

    @Mapping(target = "copy", ignore = true)
    Project<M> fromDto(ProjectDto<D> projectDto);
}

package ru.nucodelabs.gem.file.dto.project;

import ru.nucodelabs.gem.app.project.Project;

public abstract class AbstractProjectDtoMapper<M, D> {
    public abstract ProjectDto<D> toDto(Project<M> project);

    public abstract Project<M> fromDto(ProjectDto<D> projectDto);
}

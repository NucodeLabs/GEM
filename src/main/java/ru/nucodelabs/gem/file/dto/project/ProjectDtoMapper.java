package ru.nucodelabs.gem.file.dto.project;

import org.mapstruct.Mapper;
import ru.nucodelabs.gem.app.project.Project;
import ru.nucodelabs.gem.file.dto.anisotropy.PointDto;
import ru.nucodelabs.gem.file.dto.mapper.DtoMapper;
import ru.nucodelabs.geo.anisotropy.Point;

@Mapper(componentModel = "jsr330", uses = {DtoMapper.class})
public abstract class ProjectDtoMapper {
    public abstract Project<Point> toPointProject(ProjectDto<PointDto> projectDto);

    public abstract ProjectDto<PointDto> fromPointProject(Project<Point> project);
}

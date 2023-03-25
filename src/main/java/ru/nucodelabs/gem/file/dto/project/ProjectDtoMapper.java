package ru.nucodelabs.gem.file.dto.project;

import org.mapstruct.Mapper;
import ru.nucodelabs.gem.app.io.next.Project;
import ru.nucodelabs.gem.file.dto.anisotropy.PointDto;
import ru.nucodelabs.gem.file.dto.mapper.DtoMapper;
import ru.nucodelabs.geo.anisotropy.Point;

@Mapper(componentModel = "jsr330", uses = {DtoMapper.class})
public abstract class ProjectDtoMapper {
    public abstract Project<Point> fromDto(ProjectDto<PointDto> projectDto);

    public abstract ProjectDto<PointDto> toDto(Project<Point> project);
}

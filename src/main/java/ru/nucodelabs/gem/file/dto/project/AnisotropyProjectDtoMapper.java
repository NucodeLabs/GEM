package ru.nucodelabs.gem.file.dto.project;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.nucodelabs.gem.file.dto.anisotropy.PointDto;
import ru.nucodelabs.gem.file.dto.mapper.DtoMapper;
import ru.nucodelabs.geo.anisotropy.Point;

@Mapper(
        componentModel = "jsr330",
        uses = {DtoMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR
)
public abstract class AnisotropyProjectDtoMapper implements AbstractProjectDtoMapper<Point, PointDto> {
}
package ru.nucodelabs.gem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import ru.nucodelabs.gem.app.io.anisotropy.PointProjectFileServiceImpl;
import ru.nucodelabs.gem.app.io.project.ProjectFileService;
import ru.nucodelabs.gem.file.dto.project.ProjectDtoMapper;
import ru.nucodelabs.geo.anisotropy.Point;

public class ProjectModule extends AbstractModule {
    @Provides
    ProjectFileService<Point> projectFileService(ObjectMapper objectMapper, ProjectDtoMapper projectDtoMapper) {
        return new PointProjectFileServiceImpl(objectMapper, projectDtoMapper);
    }
}

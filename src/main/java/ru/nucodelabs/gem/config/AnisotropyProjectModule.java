package ru.nucodelabs.gem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import ru.nucodelabs.gem.app.io.anisotropy.PointProjectFileServiceImpl;
import ru.nucodelabs.gem.app.io.project.Project;
import ru.nucodelabs.gem.app.io.project.ProjectFileService;
import ru.nucodelabs.gem.file.dto.project.ProjectDtoMapper;
import ru.nucodelabs.geo.anisotropy.Point;
import ru.nucodelabs.kfx.snapshot.HistoryManager;

public class AnisotropyProjectModule extends AbstractModule {
    @Provides
    @Singleton
    ProjectFileService<Point> projectFileService(ObjectMapper objectMapper, ProjectDtoMapper projectDtoMapper) {
        return new PointProjectFileServiceImpl(objectMapper, projectDtoMapper);
    }

    @Provides
    @Singleton
    HistoryManager<Project<Point>> historyManager(Project<Point> project) {
        return new HistoryManager<>(project);
    }
}

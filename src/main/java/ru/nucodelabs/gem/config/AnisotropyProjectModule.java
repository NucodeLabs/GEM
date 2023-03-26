package ru.nucodelabs.gem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import ru.nucodelabs.gem.app.project.PointProjectFileServiceImpl;
import ru.nucodelabs.gem.app.project.Project;
import ru.nucodelabs.gem.app.project.ProjectFileService;
import ru.nucodelabs.gem.file.dto.project.AnisotropyProjectDtoMapper;
import ru.nucodelabs.geo.anisotropy.Point;
import ru.nucodelabs.kfx.snapshot.HistoryManager;

public class AnisotropyProjectModule extends AbstractModule {
    @Provides
    @Named("initial")
    @Singleton
    Project<Point> pointProject() {
        return new Project<>(new Point());
    }


    @Provides
    @Singleton
    ProjectFileService<Point> projectFileService(ObjectMapper objectMapper, AnisotropyProjectDtoMapper projectDtoMapper) {
        return new PointProjectFileServiceImpl(objectMapper, projectDtoMapper);
    }

    @Provides
    @Singleton
    HistoryManager<Project<Point>> historyManager(@Named("initial") Project<Point> project) {
        return new HistoryManager<>(project);
    }
}

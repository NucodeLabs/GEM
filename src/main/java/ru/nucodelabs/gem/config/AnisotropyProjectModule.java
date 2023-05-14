package ru.nucodelabs.gem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import ru.nucodelabs.gem.app.project.Project;
import ru.nucodelabs.gem.app.project.ProjectFileService;
import ru.nucodelabs.gem.app.project.ProjectSnapshotService;
import ru.nucodelabs.gem.app.project.impl.anisotropy.PointProjectFileServiceImpl;
import ru.nucodelabs.gem.app.project.impl.anisotropy.PointProjectSnapshotServiceImpl;
import ru.nucodelabs.gem.file.dto.project.AnisotropyProjectDtoMapper;
import ru.nucodelabs.gem.fxmodel.anisotropy.app.AnisotropyFxAppModel;
import ru.nucodelabs.geo.anisotropy.Point;
import ru.nucodelabs.kfx.snapshot.HistoryManager;

public class AnisotropyProjectModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AnisotropyFxAppModel.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    @Named(ArgNames.INITIAL)
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
    ProjectSnapshotService<Point> projectSnapshotService(
            @Named(ArgNames.INITIAL) Project<Point> project,
            AnisotropyProjectDtoMapper dtoMapper
    ) {
        return new PointProjectSnapshotServiceImpl(project, dtoMapper);
    }

    @Provides
    @Singleton
    HistoryManager<Project<Point>> historyManager(ProjectSnapshotService<Point> projectSnapshotService) {
        return new HistoryManager<>(projectSnapshotService);
    }
}

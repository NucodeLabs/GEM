package ru.nucodelabs.gem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import ru.nucodelabs.gem.app.project.Project;
import ru.nucodelabs.gem.app.project.ProjectContext;
import ru.nucodelabs.gem.app.project.ProjectFileService;
import ru.nucodelabs.gem.app.project.ProjectSnapshotService;
import ru.nucodelabs.gem.app.project.impl.anisotropy.PointProjectFileServiceImpl;
import ru.nucodelabs.gem.app.project.impl.anisotropy.PointProjectSnapshotServiceImpl;
import ru.nucodelabs.gem.file.dto.project.AnisotropyProjectDtoMapper;
import ru.nucodelabs.gem.fxmodel.anisotropy.app.AnisotropyFxAppModel;
import ru.nucodelabs.gem.fxmodel.anisotropy.app.ReloadService;
import ru.nucodelabs.geo.anisotropy.Point;
import ru.nucodelabs.kfx.snapshot.HistoryManager;
import ru.nucodelabs.kfx.snapshot.Snapshot;

public class AnisotropyProjectModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AnisotropyFxAppModel.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    ProjectContext<Point> projectContext() {
        return new ProjectContext<>(AnisotropyFxAppModel.Defaults.getNewProject());
    }


    @Provides
    @Singleton
    ProjectFileService<Point> projectFileService(ObjectMapper objectMapper, AnisotropyProjectDtoMapper projectDtoMapper) {
        return new PointProjectFileServiceImpl(objectMapper, projectDtoMapper);
    }

    @Provides
    @Singleton
    ProjectSnapshotService<Point> projectSnapshotService(
            ProjectContext<Point> projectContext,
            AnisotropyProjectDtoMapper dtoMapper
    ) {
        return new PointProjectSnapshotServiceImpl(projectContext, dtoMapper);
    }

    @Provides
    @Singleton
    Snapshot.Originator<Project<Point>> originator(ProjectSnapshotService<Point> snapshotService) {
        return snapshotService;
    }

    @Provides
    @Singleton
    HistoryManager<Project<Point>> historyManager(Snapshot.Originator<Project<Point>> originator) {
        return new HistoryManager<>(originator);
    }

    @Provides
    @Singleton
    ReloadService<Point> reloadService(HistoryManager<Project<Point>> historyManager, Snapshot.Originator<Project<Point>> originator) {
        return new ReloadService<>(historyManager, originator);
    }
}

package ru.nucodelabs.gem.view.controller.anisotropy.main

import javafx.scene.layout.VBox
import ru.nucodelabs.gem.app.io.project.ProjectFileService
import ru.nucodelabs.geo.anisotropy.Point
import ru.nucodelabs.kfx.core.AbstractViewController
import javax.inject.Inject

class AnisotropyMainViewController @Inject constructor(
    private val pointProjectFileService: ProjectFileService<Point>
) : AbstractViewController<VBox>()
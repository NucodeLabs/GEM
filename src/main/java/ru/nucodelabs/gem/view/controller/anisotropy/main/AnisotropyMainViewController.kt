package ru.nucodelabs.gem.view.controller.anisotropy.main

import javafx.scene.layout.VBox
import ru.nucodelabs.gem.fxmodel.anisotropy.app.AnisotropyFxAppModel
import ru.nucodelabs.kfx.core.AbstractViewController
import javax.inject.Inject

class AnisotropyMainViewController @Inject constructor(
    private val appModel: AnisotropyFxAppModel,
) : AbstractViewController<VBox>() {

}
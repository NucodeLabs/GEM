package ru.nucodelabs.gem.view.main

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import ru.nucodelabs.gem.fxmodel.ObservableSection
import ru.nucodelabs.geo.ves.Section
import ru.nucodelabs.gem.app.io.StorageManager
import ru.nucodelabs.gem.app.snapshot.HistoryManager
import ru.nucodelabs.gem.app.snapshot.Snapshot
import ru.nucodelabs.gem.view.DialogsModule
import ru.nucodelabs.gem.view.charts.ChartsModule

class MainViewModule : AbstractModule() {
    override fun configure() {
        bind(MainViewController::class.java).`in`(Singleton::class.java)
        bind(StorageManager::class.java).`in`(Singleton::class.java)
        bind(ObservableSection::class.java).`in`(Singleton::class.java)

        install(DialogsModule())
        install(ObservableDataModule())
        install(ChartsModule())
    }

    @Provides
    @Singleton
    fun sectionHistoryManager(sectionOriginator: Snapshot.Originator<Section>): HistoryManager<Section> =
        HistoryManager(sectionOriginator)

    @Provides
    fun sectionOriginator(observableSection: ObservableSection): Snapshot.Originator<Section> = observableSection

    @Provides
    fun fileImporter(mainViewController: MainViewController): FileImporter = mainViewController

    @Provides
    fun fileOpener(mainViewController: MainViewController): FileOpener = mainViewController
}

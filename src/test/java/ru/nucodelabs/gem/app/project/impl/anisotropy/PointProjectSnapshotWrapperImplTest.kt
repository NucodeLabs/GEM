package ru.nucodelabs.gem.app.project.impl.anisotropy

import com.google.inject.Guice
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import ru.nucodelabs.gem.app.project.Project
import ru.nucodelabs.gem.app.project.ProjectContext
import ru.nucodelabs.gem.app.project.impl.anisotropy.cloner.PointProjectCloner
import ru.nucodelabs.gem.config.MappersModule
import ru.nucodelabs.geo.anisotropy.Point

class PointProjectSnapshotWrapperImplTest {

    private val project = Project(
        data = Point()
    )

    private val projectContext = ProjectContext(project)

    private val mapper = Guice.createInjector(MappersModule()).getInstance(PointProjectCloner::class.java)

    private val uut = PointProjectSnapshotWrapperImpl(projectContext, mapper)

    @Test
    fun test() {
        val snapshot = uut.snapshot()
        project.data.comment = "Modified"
        assertNotEquals(project, snapshot.value)

        val modifiedSnapshot = uut.snapshot()
        uut.restoreFromSnapshot(modifiedSnapshot)
        assertEquals(project, modifiedSnapshot.value)
    }
}
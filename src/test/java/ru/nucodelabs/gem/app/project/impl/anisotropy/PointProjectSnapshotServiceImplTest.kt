package ru.nucodelabs.gem.app.project.impl.anisotropy

import com.google.inject.Guice
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import ru.nucodelabs.gem.app.project.Project
import ru.nucodelabs.gem.config.MappersModule
import ru.nucodelabs.gem.file.dto.project.AnisotropyProjectDtoMapper
import ru.nucodelabs.geo.anisotropy.Point

class PointProjectSnapshotServiceImplTest {

    private val project = Project(
        data = Point()
    )

    private val mapper = Guice.createInjector(MappersModule()).getInstance(AnisotropyProjectDtoMapper::class.java)

    private val uut = PointProjectSnapshotServiceImpl(project, mapper)

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
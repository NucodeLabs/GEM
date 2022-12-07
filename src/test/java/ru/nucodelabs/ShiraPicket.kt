package ru.nucodelabs

import ru.nucodelabs.geo.ves.Picket
import ru.nucodelabs.gem.app.io.SonetImportManager.Factory.create
import java.io.File

object ShiraPicket {
    /**
     * Делает готовый Shira пикет, с готовой моделью
     *
     * @return Пикет Shira
     */
    @JvmStatic
    @get:Throws(Exception::class)
    val picket: Picket
        get() {
            val expFile = File("data/SHIRA.EXP")
            val modFile = File("data/SHIRA_M2.mod")
            val sonetImportManager = create()
            val picket = sonetImportManager.fromEXPFile(expFile)
            return picket.copy(modelData = sonetImportManager.fromMODFile(modFile))
        }
}
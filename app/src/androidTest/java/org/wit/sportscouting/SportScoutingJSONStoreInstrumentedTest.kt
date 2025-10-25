package org.wit.sportscouting

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.wit.sportscouting.main.MainApp
import org.wit.sportscouting.models.SportScoutingJSONStore
import org.wit.sportscouting.models.SportScoutingModel
import java.io.File

@RunWith(AndroidJUnit4::class)
class SportScoutingJSONStoreInstrumentedTest {

    private lateinit var app: MainApp
    private lateinit var store: SportScoutingJSONStore
    private lateinit var dataFile: File
    private lateinit var backupFile: File

    @Before
    fun setUp() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        app = ctx.applicationContext as MainApp
        store = SportScoutingJSONStore(ctx)

        dataFile = File(ctx.filesDir, "sportscouting.json")
        backupFile = File(ctx.filesDir, "sportscouting_backup_test.json")

        // Backup if a user JSON already exists and clean the file for the test
        if (dataFile.exists()) {
            dataFile.copyTo(backupFile, overwrite = true)
            dataFile.delete()
        }
    }

    @After
    fun tearDown() {
        // Restore the backup if it existed. If it doesn't exist, leave it clean.
        if (backupFile.exists()) {
            backupFile.copyTo(dataFile, overwrite = true)
            backupFile.delete()
        } else {
            if (dataFile.exists()) dataFile.delete()
        }
    }

    @Test
    fun load_whenNoLocalFile_usesAssets() {
        // The local file must not exist
        assertFalse(dataFile.exists())

        val items = store.load()

        // upload sample_players.json
        assertTrue(items.isNotEmpty())
        // The first player is "Vinicius" (like in sample_players.json)
        assertEquals("Vinicius", items.first().title)
    }

    @Test
    fun saveAll_thenLoad_roundTrip() {
        val original = listOf(
            SportScoutingModel("Kane", "Bayern", "forward"),
            SportScoutingModel("Modric", "Milan", "midfielder")
        )

        // save JSON
        store.saveAll(original)
        assertTrue("The JSON file should exist after saving", dataFile.exists())

        // Reload and compare content
        val loaded = store.load()
        assertEquals(original.size, loaded.size)
        assertEquals(original, loaded)
    }
}

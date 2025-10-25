package org.wit.sportscouting

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.wit.sportscouting.main.MainApp

@RunWith(AndroidJUnit4::class)
class MainAppInstrumentedTest {

    @Test
    fun useAppContext_isCorrect() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("org.wit.sportscouting", appContext.packageName)
    }

    @Test
    fun persist_createsJsonFile() {
        val app = InstrumentationRegistry.getInstrumentation()
            .targetContext.applicationContext as MainApp

        app.sportscoutings.add(
            org.wit.sportscouting.models.SportScoutingModel("Test", "JUnit", "forward")
        )
        app.persist()

        val file = app.filesDir.resolve("sportscouting.json")
        assertTrue("The JSON file should exist after saving", file.exists())
    }
}

package org.wit.sportscouting

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.wit.sportscouting.models.SportScoutingModel

class SportScoutingModelTest {

    @Test
    fun createPlayer_isCorrect() {
        val player = SportScoutingModel("Mbappe", "Real Madrid", "forward")

        assertEquals("Mbappe", player.title)
        assertEquals("Real Madrid", player.description)
        assertEquals("forward", player.position)
    }

    @Test
    fun compareTwoPlayers_areDifferent() {
        val p1 = SportScoutingModel("Pedri", "Barcelona", "midfielder")
        val p2 = SportScoutingModel("Mbappe", "Real Madrid", "forward")

        assertNotEquals(p1, p2)
    }
}

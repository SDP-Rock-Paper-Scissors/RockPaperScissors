package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.models.Game.GameMode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GameTest {
    @Test
    fun test() {
        val m1 = GameMode(playerCount = 2, type = GameMode.Type.PVP, rounds = 3, timeLimit = 10)

        assertEquals(GameMode.fromString(m1.toGameModeString()), m1)


        val m2 = GameMode(playerCount = 2, type = GameMode.Type.PVP, rounds = 3, timeLimit = 0)

        assertEquals(GameMode.fromString(m2.toGameModeString()), m2)
    }
}
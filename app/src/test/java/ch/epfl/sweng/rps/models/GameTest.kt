package ch.epfl.sweng.rps.models

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId


class GameTest {

    private val json = Json { prettyPrint = true }

    @Test
    fun serializationTest() {
        val date =
            LocalDate.of(2022, 3, 9)
                .atTime(17, 30)
                .atZone(ZoneId.of("Europe/Zurich")).plusNanos(1000)

        val game = Game(
            uid = "this_is_my_game",
            mode = Game.Mode(
                playerCount = 3,
                type = Game.Mode.Type.PC,
                time = null,
                rounds = 2
            ),
            players = listOf(
                Game.Uid("gaetan_s", false),
                Game.Uid("pc_1", true),
                Game.Uid("pc_2", true),
            )
        )
        val element = json.encodeToJsonElement(Game.serializer(), game)
        print(element)
        val game2 = json.decodeFromJsonElement(Game.serializer(), element)
        assertEquals("The serialization-deserialization alters data.", game2, game)
    }
}
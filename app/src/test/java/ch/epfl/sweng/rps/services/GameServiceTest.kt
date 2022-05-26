package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.services.GameService.GameServiceException
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class GameServiceTest {

    @Test
    fun exception() {
        assertThrows(GameServiceException::class.java) {
            throw GameServiceException("a")
        }

        assertThrows(GameServiceException::class.java) {
            throw GameServiceException("a", Exception())
        }

        assertThrows(GameServiceException::class.java) {
            throw GameServiceException(Exception())
        }
    }
}
package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.TestUtils.initializeForTest
import ch.epfl.sweng.rps.models.GameMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.ktx.Firebase
import io.mockk.mockk
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MatchmakingServiceTest {

    @Before
    fun setUp() {
        Firebase.initializeForTest()
        FirebaseAuth.getInstance().signOut()
    }

    @Test
    fun queue() {
        val matchmakingService = MatchmakingService()
        assertThrows(FirebaseFunctionsException::class.java) {
            runBlocking {
                matchmakingService.queue(
                    GameMode(
                        2,
                        GameMode.Type.PVP,
                        3,
                        0,
                        GameMode.GameEdition.RockPaperScissors
                    )
                ).collect()
            }
        }
    }

    @Test
    fun queueStatus() {
        val status: () -> MatchmakingService.QueueStatus =
            { MatchmakingService.QueueStatus.Queued(mockk()) }
        assertTrue(
            when (status()) {
                is MatchmakingService.QueueStatus.Queued -> true
                is MatchmakingService.QueueStatus.GameJoined -> false
            }
        )
        val status2: () -> MatchmakingService.QueueStatus = {
            MatchmakingService.QueueStatus.GameJoined(mockk())
        }
        assertTrue(
            when (status2()) {
                is MatchmakingService.QueueStatus.Queued -> false
                is MatchmakingService.QueueStatus.GameJoined -> true
            }
        )
    }
}
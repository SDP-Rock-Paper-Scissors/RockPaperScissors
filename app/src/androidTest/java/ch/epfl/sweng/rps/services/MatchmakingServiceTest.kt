package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.TestUtils.initializeForTest
import ch.epfl.sweng.rps.db.LocalRepository
import ch.epfl.sweng.rps.models.Game
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.ktx.Firebase
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
                matchmakingService.queue(Game.GameMode(2, Game.GameMode.Type.PVP, 3, 0)).collect()
            }
        }
    }

    @Test
    fun queueStatus() {
        val status: () -> MatchmakingService.QueueStatus = { MatchmakingService.QueueStatus.Queued }
        when (status()) {
            MatchmakingService.QueueStatus.Queued -> assertTrue(true)
            is MatchmakingService.QueueStatus.Accepted -> assertTrue(false)
        }
        val status2: () -> MatchmakingService.QueueStatus = {
            MatchmakingService.QueueStatus.Accepted(
                OfflineGameService(
                    "",
                    LocalRepository(),
                    listOf(),
                    Game.GameMode(2, Game.GameMode.Type.PVP, 3, 0)
                )
            )
        }
        when (status2()) {
            MatchmakingService.QueueStatus.Queued -> assertTrue(false)
            is MatchmakingService.QueueStatus.Accepted -> assertTrue(true)
        }
    }
}
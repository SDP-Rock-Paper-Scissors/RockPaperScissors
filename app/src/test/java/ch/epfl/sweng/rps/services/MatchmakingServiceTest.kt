package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.models.remote.GameMode
import ch.epfl.sweng.rps.remote.Env
import ch.epfl.sweng.rps.utils.L
import com.google.android.gms.tasks.Tasks
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableReference
import com.google.firebase.functions.HttpsCallableResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MatchmakingServiceTest {

    data class Cfg(
        val queuedGameId: (GameMode) -> String,
        val invitedPlayerGameId: (String, GameMode) -> String,
        val inviteAcceptedGameId: (String) -> String
    )

    private val cfg = Cfg(
        { "queued_game_id_$it" },
        { a1, a2 -> "invited_player_game_id_${a1}_$a2" },
        { "invite_accepted_game_id_$it" },
    )

    @Test
    fun queue() = runBlocking {
        val matchmakingService = matchmakingService(cfg)
        val l = mutableListOf<MatchmakingService.QueueStatus>()
        val gm = GameMode.default(2)
        matchmakingService.queue(gm).toCollection(l)
        assertTrue { l.count { it is MatchmakingService.QueueStatus.Queued } == 1 }
        assertTrue {
            l.count {
                it is MatchmakingService.QueueStatus.GameJoined && it.gameService.gameId == cfg.queuedGameId(gm)
            } == 1
        }
    }

    @Test
    fun invite() = runBlocking {
        val matchmakingService = matchmakingService(cfg)
        val args = Pair("u1", GameMode.default(5))
        val gameId = matchmakingService.invitePlayer(args.first, args.second)
        assertEquals(cfg.invitedPlayerGameId(args.first, args.second), gameId.gameId)
    }

    @Test
    fun acceptInvite() = runBlocking {
        val matchmakingService = matchmakingService(cfg)
        val invId = "invite_id1"
        val inv = matchmakingService.acceptInvitation(invId)
        assertEquals(cfg.inviteAcceptedGameId(invId), inv.gameId)
    }

    private fun matchmakingService(cfg: Cfg): MatchmakingService {

        ServiceLocator.setCurrentEnv(Env.Test)
        val cloudFunctions = mockk<MatchmakingService.CloudFunctions>()
        val log = mockk<L.LogService>()
        every { log.d(any()) } returns Unit
        every { log.e(any()) } returns Unit
        every { log.i(any()) } returns Unit
        every { log.w(any()) } returns Unit

        val s1 = slot<GameMode>()
        coEvery { cloudFunctions.queue(capture(s1)) } answers {
            cfg.queuedGameId(s1.captured)
        }

        val (s2, s3) = Pair(slot<String>(), slot<GameMode>())
        coEvery {
            cloudFunctions.invitePlayer(
                capture(s2), capture(s3)
            )
        } answers {
            cfg.invitedPlayerGameId(s2.captured, s3.captured)
        }

        val s4 = slot<String>()
        coEvery { cloudFunctions.acceptInvitation(capture(s4)) } answers {
            cfg.inviteAcceptedGameId(s4.captured)
        }
        assertEquals(Env.Test, ServiceLocator.getCurrentEnv())
        (ServiceLocator.getInstance(Env.Test) as TestServiceLocator).setGameServiceFn { gameId, _ ->
            val gs = mockk<FirebaseGameService>()
            every { gs.gameId } returns gameId
            gs
        }

        return MatchmakingService(cloudFunctions, log)
    }

    @Test
    fun testCloudFunctions() = runBlocking {
        val firebaseFunctions = mockk<FirebaseFunctions>()
        val callable = mockk<HttpsCallableReference>()
        val result = mockk<HttpsCallableResult>()
        val res = "dummy"
        every { result.data } returns res
        every { callable.call(any()) } returns Tasks.forResult(result)
        every { firebaseFunctions.getHttpsCallable(any()) } returns callable

        val cloudFunctions = MatchmakingService.CloudFunctions(firebaseFunctions)
        assertEquals(res, cloudFunctions.queue(GameMode.default(2)))
        assertEquals(res, cloudFunctions.invitePlayer("u1", GameMode.default(5)))
        assertEquals(res, cloudFunctions.queue(GameMode.default(2)))
    }

    @Test
    fun queueStatus() {
        val status: () -> MatchmakingService.QueueStatus = { MatchmakingService.QueueStatus.Queued(mockk()) }
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
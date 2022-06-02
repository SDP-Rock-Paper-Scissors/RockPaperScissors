package ch.epfl.sweng.rps.services

import android.util.Log
import ch.epfl.sweng.rps.models.remote.GameMode
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

open class MatchmakingService {
    private val cloudFunctions = CloudFunctions()

    open fun queue(gameMode: GameMode): Flow<QueueStatus> = flow {
        Log.i("MatchmakingService", "Queueing for game mode ${gameMode.toGameModeString()}")
        emit(QueueStatus.Queued(gameMode))
        Log.i("MatchmakingService", "Sending request to cloud function")
        val gameId = cloudFunctions.queue(gameMode)
        Log.i("MatchmakingService", "Received game id $gameId")

        val service = ServiceLocator.getInstance().getGameServiceForGame(gameId)
        Log.i("MatchmakingService", "Got service $service")
        emit(QueueStatus.GameJoined(service))
    }

    suspend fun acceptInvitation(invitationId: String): FirebaseGameService {
        val gameId = cloudFunctions.acceptInvitation(invitationId)
        return ServiceLocator.getInstance().getGameServiceForGame(gameId)
    }

    open suspend fun currentGame(): FirebaseGameService? {
        val repo = ServiceLocator.getInstance().repository
        val games = repo.games.myActiveGames()
        if (games.isEmpty()) {
            return null
        }
        if (games.size > 1) {
            Log.e(
                "MatchmakingService",
                "More than one game for user ${repo.getCurrentUid()}: ${games.map { it.id }}"
            )
        }
        return ServiceLocator.getInstance().getGameServiceForGame(games[0].id)
    }

    /**
     * Cancel the current queue if any. For now this doesn't do anything.
     */
    suspend fun cancelQueue() {
        delay(100)
    }

    sealed class QueueStatus {
        class Queued(val gameMode: GameMode) : QueueStatus()
        class GameJoined(val gameService: FirebaseGameService) : QueueStatus()
    }

    class CloudFunctions {
        private val functions get() = Firebase.functions("europe-west1")

        suspend fun queue(gameMode: GameMode): String {
            val res = functions.getHttpsCallable("queue").call(
                hashMapOf(
                    "game_mode" to gameMode.toGameModeString()
                )
            ).await()
            return res.data as String
        }

        suspend fun invitePlayer(gameMode: GameMode, userId: String): String {
            val res = functions.getHttpsCallable("invite_player").call(
                hashMapOf(
                    "game_mode" to gameMode.toGameModeString(),
                    "user_id" to userId
                )
            ).await()
            return res.data as String
        }

        suspend fun acceptInvitation(invitationId: String): String {
            return functions.getHttpsCallable("accept_invitation").call(
                hashMapOf(
                    "invitation_id" to invitationId
                )
            ).await().data as String
        }
    }
}
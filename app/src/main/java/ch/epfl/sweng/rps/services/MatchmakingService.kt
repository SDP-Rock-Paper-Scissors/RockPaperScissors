package ch.epfl.sweng.rps.services

import androidx.annotation.VisibleForTesting
import ch.epfl.sweng.rps.models.remote.GameMode
import ch.epfl.sweng.rps.utils.L
import ch.epfl.sweng.rps.utils.europeWest1
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

/**
 * This class is responsible for the communication with the cloud functions managing matchmaking.
 */
open class MatchmakingService(
    @VisibleForTesting internal val cloudFunctions: CloudFunctions = CloudFunctions(),
    @VisibleForTesting internal val log: L.LogService = L.of(MatchmakingService::class.java)
) {


    /**
     * This function is used to create a matchmaking request.
     */
    open fun queue(gameMode: GameMode): Flow<QueueStatus> = flow {
        log.i("Queueing for game mode ${gameMode.toGameModeString()}")
        emit(QueueStatus.Queued(gameMode))
        log.i("Sending request to cloud function")
        val gameId = cloudFunctions.queue(gameMode)
        log.i("Received game id $gameId")

        val service = ServiceLocator.getInstance().getGameServiceForGame(gameId)
        log.i("Got service $service")
        emit(QueueStatus.GameJoined(service))
    }

    /**
     * This function is used to accept an invitation to a game.
     */
    suspend fun acceptInvitation(invitationId: String): FirebaseGameService {
        val gameId = cloudFunctions.acceptInvitation(invitationId)
        return ServiceLocator.getInstance().getGameServiceForGame(gameId)
    }


    /**
     * Invites a player to a game of the given [gameMode].
     */
    suspend fun invitePlayer(userId: String, gameMode: GameMode): FirebaseGameService {
        val gameId = cloudFunctions.invitePlayer(userId = userId, gameMode = gameMode)
        return ServiceLocator.getInstance().getGameServiceForGame(gameId)
    }


    /**
     * Declines an invitation to a game.
     */
    suspend fun declineInvitation(invitationId: String) {
        cloudFunctions.declineInvitation(invitationId)
    }


    /**
     * This function returns the current game's [FirebaseGameService]
     * if there is one game in progress.
     */
    open suspend fun currentGame(): FirebaseGameService? {
        val repo = ServiceLocator.getInstance().repository
        val games = repo.games.myActiveGames()
        if (games.isEmpty()) {
            return null
        }
        if (games.size > 1) {
            log.e(
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

    /**
     * Sealed class used to represent the status of a matchmaking request.
     */
    sealed class QueueStatus {
        /**
         * When the user is queued for a game of the given [gameMode].
         */
        class Queued(
            /**
             * The game mode for which the user is queued.
             */
            val gameMode: GameMode
        ) : QueueStatus()

        /**
         * When the user has joined a game via [gameService].
         */
        class GameJoined(
            /**
             * The [FirebaseGameService] for the game.
             */
            val gameService: FirebaseGameService
        ) : QueueStatus()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    class CloudFunctions(
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) internal val functionsOverride: FirebaseFunctions? = null
    ) {
        private val functions: FirebaseFunctions by lazy { functionsOverride ?: Firebase.europeWest1 }

        suspend fun queue(gameMode: GameMode): String {
            val res = functions.getHttpsCallable("queue").call(
                hashMapOf(
                    "game_mode" to gameMode.toGameModeString()
                )
            ).await()
            return res.data as String
        }
        
        suspend fun invitePlayer(userId: String, gameMode: GameMode): String {
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

        suspend fun declineInvitation(invitationId: String) {
            functions.getHttpsCallable("decline_invitation").call(
                hashMapOf(
                    "invitation_id" to invitationId
                )
            ).await()
        }
    }
}
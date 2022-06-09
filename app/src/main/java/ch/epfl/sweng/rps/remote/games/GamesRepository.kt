package ch.epfl.sweng.rps.remote.games

import ch.epfl.sweng.rps.models.remote.Game
import ch.epfl.sweng.rps.models.remote.Invitation
import ch.epfl.sweng.rps.models.remote.TotalScore
import ch.epfl.sweng.rps.models.remote.UserStats

/**
 * Interface for the games repository
 */
interface GamesRepository {
    /**
     * Get the game with [gameId]
     */
    suspend fun getGame(gameId: String): Game?

    /**
     * Get the leaderboard score
     */
    suspend fun getLeaderBoardScore(scoreMode: String): List<TotalScore>

    /**
     * Get all the games of the user with [uid]
     */
    suspend fun gamesOfUser(uid: String): List<Game>

    /**
     * Get all the active games of the user logged in
     */
    suspend fun myActiveGames(): List<Game>

    /**
     * Get the stats of a user with [uid]
     */
    suspend fun statsOfUser(uid: String): UserStats

    /**
     * Lists all the invitations of the user logged in
     */
    suspend fun listInvitations(): List<Invitation>
}
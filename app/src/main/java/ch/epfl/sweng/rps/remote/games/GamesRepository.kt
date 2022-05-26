package ch.epfl.sweng.rps.remote.games

import ch.epfl.sweng.rps.models.remote.Game
import ch.epfl.sweng.rps.models.remote.Invitation
import ch.epfl.sweng.rps.models.remote.TotalScore
import ch.epfl.sweng.rps.models.remote.UserStats

interface GamesRepository {
    suspend fun getGame(gameId: String): Game?
    suspend fun getLeaderBoardScore(scoreMode: String): List<TotalScore>
    suspend fun gamesOfUser(uid: String): List<Game>
    suspend fun myActiveGames(): List<Game>

    suspend fun statsOfUser(uid: String): UserStats

    suspend fun listInvitations(): List<Invitation>
}
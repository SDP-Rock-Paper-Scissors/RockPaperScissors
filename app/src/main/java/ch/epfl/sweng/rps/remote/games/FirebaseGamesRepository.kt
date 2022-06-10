package ch.epfl.sweng.rps.remote.games

import ch.epfl.sweng.rps.models.remote.Game
import ch.epfl.sweng.rps.models.remote.Game.Companion.toGame
import ch.epfl.sweng.rps.models.remote.Game.Companion.toListOfGames
import ch.epfl.sweng.rps.models.remote.Invitation
import ch.epfl.sweng.rps.models.remote.TotalScore
import ch.epfl.sweng.rps.models.remote.UserStats
import ch.epfl.sweng.rps.remote.FirebaseRepository
import ch.epfl.sweng.rps.utils.toListOf
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

/**
 * Repository for games in firebase
 */
class FirebaseGamesRepository(internal val repository: FirebaseRepository) : GamesRepository {

    private val firebase get() = repository.firebase

    override suspend fun getGame(gameId: String): Game? {
        return firebase.gamesCollection.document(gameId).get().await().toGame()
    }


    override suspend fun getLeaderBoardScore(scoreMode: String): List<TotalScore> {
        return firebase.scoresCollection.orderBy(scoreMode, Query.Direction.DESCENDING).get()
            .await().documents.map {
                it.toObject<TotalScore>()!!
            }


    }

    override suspend fun gamesOfUser(uid: String): List<Game> {
        return firebase.gamesCollection.whereArrayContains(Game.FIELDS.PLAYERS, uid).get()
            .await().toListOfGames()
    }

    override suspend fun myActiveGames(): List<Game> {
        return firebase.gamesCollection
            .whereArrayContains(Game.FIELDS.PLAYERS, repository.getCurrentUid())
            .whereEqualTo(Game.FIELDS.DONE, false)
            .get().await().toListOfGames()
    }

    override suspend fun statsOfUser(uid: String): UserStats {
        return firebase.usersCollection.document(uid).collection("stats").document("games").get()
            .await().toObject<UserStats>()!!
    }

    override suspend fun listInvitations(): List<Invitation> {
        return firebase.invitationsOfUid(repository.getCurrentUid()).get()
            .await().documents.toListOf()
    }

}
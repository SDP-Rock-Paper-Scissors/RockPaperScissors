package ch.epfl.sweng.rps.remote.games

import ch.epfl.sweng.rps.models.remote.*
import ch.epfl.sweng.rps.remote.FirebaseRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

/**
 * Repository for games in firebase
 */
class FirebaseGamesRepository(internal val repository: FirebaseRepository) : GamesRepository {

    private val firebase get() = repository.firebase

    override suspend fun getGame(gameId: String): Game? {
        val doc: DocumentSnapshot = firebase.gamesCollection.document(gameId).get().await()
        return Game.fromDocumentSnapshot(doc)
    }

    override suspend fun getLeaderBoardScore(scoreMode: String): List<TotalScore> {
        return firebase.scoresCollection.orderBy(scoreMode, Query.Direction.DESCENDING).get()
            .await().documents.map {
                it.toObject<TotalScore>()!!
            }


    }

    override suspend fun gamesOfUser(uid: String): List<Game> {
        return firebase.gamesCollection.whereArrayContains(Game.FIELDS.PLAYERS, uid).get()
            .await().documents.map { Game.fromDocumentSnapshot(it)!! }
    }

    override suspend fun myActiveGames(): List<Game> {
        return firebase.gamesCollection
            .whereArrayContains(Game.FIELDS.PLAYERS, repository.getCurrentUid())
            .whereEqualTo(Game.FIELDS.DONE, false)
            .get().await().documents.map { Game.fromDocumentSnapshot(it)!! }
    }

    override suspend fun statsOfUser(uid: String): UserStats {
        return firebase.usersCollection.document(uid).collection("stats").document("games").get()
            .await().toObject<UserStats>()!!
    }

    override suspend fun listInvitations(): List<Invitation> {
        return firebase.invitationsCollection
            .whereArrayContains(Invitation.FIELDS.UIDS, repository.getCurrentUid())
            .whereEqualTo(Invitation.FIELDS.STATUS, FriendRequest.Status.PENDING)
            .get().await().toObjects(Invitation::class.java)
    }

}
package ch.epfl.sweng.rps.remote.friends

import ch.epfl.sweng.rps.models.remote.FriendRequest

interface FriendsRepository {
    suspend fun sendFriendRequestTo(uid: String)
    suspend fun listFriendRequests(): List<FriendRequest>
    suspend fun getFriends(): List<String>
    suspend fun changeFriendRequestToStatus(userUid: String, status: FriendRequest.Status)
    suspend fun acceptFriendRequest(userUid: String) =
        changeFriendRequestToStatus(userUid, FriendRequest.Status.ACCEPTED)
}
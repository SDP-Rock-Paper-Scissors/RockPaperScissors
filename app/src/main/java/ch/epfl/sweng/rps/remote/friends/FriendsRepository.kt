package ch.epfl.sweng.rps.remote.friends

import ch.epfl.sweng.rps.models.remote.FriendRequest

/**
 * Repository for friends.
 */
interface FriendsRepository {
    /**
     * Send a friend request to the given user
     */
    suspend fun sendFriendRequestTo(uid: String)

    /**
     * Lists all the friend requests to the current user
     */
    suspend fun listFriendRequests(): List<FriendRequest>


    /**
     * Lists all the friends of the current user.
     * Effectively, this is the list of all friend requests
     * from/to the current user that have been accepted.
     */
    suspend fun getFriends(): List<String>

    /**
     * Changes the status of the friend request from [userUid] to [status].
     */
    suspend fun changeFriendRequestToStatus(userUid: String, status: FriendRequest.Status)

    /**
     * Accepts the friend request from [userUid].
     */
    suspend fun acceptFriendRequest(userUid: String) =
        changeFriendRequestToStatus(userUid, FriendRequest.Status.ACCEPTED)
}
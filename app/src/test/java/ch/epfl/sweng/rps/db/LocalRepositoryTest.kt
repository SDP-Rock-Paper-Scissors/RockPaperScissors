package ch.epfl.sweng.rps.db

import ch.epfl.sweng.rps.db.Repository.UserNotLoggedIn
import ch.epfl.sweng.rps.models.remote.Game
import ch.epfl.sweng.rps.models.remote.Invitation
import ch.epfl.sweng.rps.models.remote.User
import com.google.firebase.Timestamp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class LocalRepositoryTest {

    @Test
    fun testUpdateUser() = runTest(UnconfinedTestDispatcher()) {
        val localRepository = LocalRepository()
        localRepository.setCurrentUid("user1234")
        assertEquals("user1234", localRepository.getCurrentUid())

        localRepository.createThisUser("User", "user@company.org")
        val user = localRepository.getUser(localRepository.getCurrentUid())
        assertEquals("User", user.username)
        assertEquals("user@company.org", user.email)
        assertEquals("user1234", user.uid)
        assertEquals(User.Privacy.PUBLIC, user.gamesHistoryPrivacyEnum())
        assertEquals(false, user.has_profile_photo)


        localRepository.updateUser(
            User.Field.USERNAME to "NewUser",
            User.Field.EMAIL to "example@example.com",
            User.Field.GAMES_HISTORY_PRIVACY to User.Privacy.PRIVATE.name,
            User.Field.HAS_PROFILE_PHOTO to true,
        )
        val updatedUser = localRepository.getUser(localRepository.getCurrentUid())
        assertEquals("NewUser", updatedUser.username)
        assertEquals("example@example.com", updatedUser.email)
        assertEquals("user1234", updatedUser.uid)
        assertEquals(User.Privacy.PRIVATE, updatedUser.gamesHistoryPrivacyEnum())
        assertEquals(true, updatedUser.has_profile_photo)
    }

    @Test
    fun testCreateUser() = runTest(UnconfinedTestDispatcher()) {
        val localRepository = LocalRepository()
        localRepository.setCurrentUid("user1234")
        assertEquals("user1234", localRepository.getCurrentUid())

        localRepository.createThisUser("User", "user@company.org")
        val user = localRepository.getUser(localRepository.getCurrentUid())
        assertEquals("User", user.username)
        assertEquals("user@company.org", user.email)

        localRepository.setCurrentUid("user5678")
        assertEquals("user5678", localRepository.getCurrentUid())

        localRepository.createThisUser(null, "email@email.com")
        val user2 = localRepository.getUser(localRepository.getCurrentUid())
        assertEquals("", user2.username)
        assertEquals("email@email.com", user2.email)
    }

    @Test
    fun testFriendRequests() = runTest(UnconfinedTestDispatcher()) {
        val u1 = "user1"
        val u2 = "user2"
        val localRepository = LocalRepository()

        localRepository.setCurrentUid(u1)
        localRepository.sendFriendRequestTo(u2)

        localRepository.setCurrentUid(u2)
        localRepository.acceptFriendRequest(u1)

        assertTrue(localRepository.getFriends().contains(u1))
        assertEquals(1, localRepository.getFriends().size)
    }

    @Test
    fun testFriendRequests2() = runTest(UnconfinedTestDispatcher()) {
        val u1 = "user1"
        val u2 = "user2"
        val localRepository = LocalRepository()

        localRepository.setCurrentUid(u1)
        localRepository.sendFriendRequestTo(u2)

        localRepository.setCurrentUid(u2)
        val friendRequest = localRepository.listFriendRequests().find { it.from == u1 }
        localRepository.acceptFriendRequest(friendRequest!!.from)

        assertTrue(localRepository.getFriends().contains(u1))
        assertEquals(1, localRepository.getFriends().size)
    }

    @Test
    fun uidTest() {
        val localRepository = LocalRepository()
        localRepository.setCurrentUid("user1")

        assertEquals("user1", localRepository.getCurrentUid())
        assertEquals("user1", localRepository.rawCurrentUid())
        assertTrue(localRepository.isLoggedIn)

        localRepository.setCurrentUid(null)

        assertEquals(null, localRepository.rawCurrentUid())
        assertThrows(UserNotLoggedIn::class.java) { localRepository.getCurrentUid() }
        assertFalse(localRepository.isLoggedIn)
    }


    @Test
    fun profilePictureTest() {
        runBlocking {
            val localRepository = LocalRepository()
            localRepository.setCurrentUid("user1")
            localRepository.users["user1"] = User("user1", "user1", has_profile_photo = true)
            val url = localRepository.getUserProfilePictureUrl("user1")
            assertNotNull(url)

            localRepository.users["user1"] = User("user1", "user1", has_profile_photo = false)
            val url2 = localRepository.getUserProfilePictureUrl("user1")
            assertNull(url2)
        }
    }

    @Test
    fun games() {
        runBlocking {
            val localRepository = LocalRepository()
            localRepository.setCurrentUid("user1")
            assertEquals(listOf<Game>(), localRepository.gamesOfUser("user1"))
            assertNull(localRepository.getGame("game1"))
        }
    }

    @Test
    fun listInvitations() {
        runBlocking {
            val localRepository = LocalRepository()
            localRepository.setCurrentUid("user1")
            assertEquals(listOf<Invitation>(), localRepository.listInvitations())
            localRepository.invitations["invitation1"] =
                Invitation("game1", Timestamp.now(), "user2", "invitation1")
            assertEquals(1, localRepository.listInvitations().size)
            assertEquals("invitation1", localRepository.listInvitations()[0].id)
            assertEquals("game1", localRepository.listInvitations()[0].game_id)
            assertEquals("user2", localRepository.listInvitations()[0].from)
            assertTrue(localRepository.listInvitations()[0].timestamp.seconds > 0)
        }
    }
}


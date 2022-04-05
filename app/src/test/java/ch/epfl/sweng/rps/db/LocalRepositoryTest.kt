package ch.epfl.sweng.rps.db

import ch.epfl.sweng.rps.db.Repository.UserNotLoggedIn
import ch.epfl.sweng.rps.models.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

        localRepository.updateUser(
            User.Field.USERNAME to "NewUser",
            User.Field.EMAIL to "example@example.com"
        )
        val updatedUser = localRepository.getUser(localRepository.getCurrentUid())
        assertEquals("NewUser", updatedUser.username)
        assertEquals("example@example.com", updatedUser.email)
        assertEquals("user1234", updatedUser.uid)
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
    }

    @Test
    fun testFriendRequests() = runTest(UnconfinedTestDispatcher()) {
        val u1 = "user1"
        val u2 = "user2"
        val localRepository = LocalRepository()

        localRepository.setCurrentUid(u1)
        localRepository.sendFriendRequestTo(u2)

        localRepository.setCurrentUid(u2)
        localRepository.acceptFriendRequestFrom(u1)

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
        localRepository.acceptFriendRequestFrom(friendRequest!!)

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
}


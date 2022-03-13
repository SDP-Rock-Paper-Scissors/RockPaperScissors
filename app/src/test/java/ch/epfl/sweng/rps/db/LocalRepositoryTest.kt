package ch.epfl.sweng.rps.db

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
class LocalRepositoryTest {

    @Test
    fun testCreateUser() = runTest(UnconfinedTestDispatcher()) {
        val localRepository = LocalRepository()
        localRepository.setCurrentUid("user1234")
        assertEquals("user1234", localRepository.getCurrentUid())

        localRepository.createUser("User", "user@company.org")
        val user = localRepository.getUser(localRepository.getCurrentUid())
        assertEquals("User", user.username)
        assertEquals("user@company.org", user.email)
    }

    @Test
    fun testFriendRequests() = runTest(UnconfinedTestDispatcher()) {
        val localRepository = LocalRepository()
        localRepository.setCurrentUid("user1")
        localRepository.sendFriendRequest("user2")
        localRepository.setCurrentUid("user2")
        localRepository.acceptFriendRequest("user1")
    }
}


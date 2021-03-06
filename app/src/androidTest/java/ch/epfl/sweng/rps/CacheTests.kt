package ch.epfl.sweng.rps

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.TestUtils.initializeForTest
import ch.epfl.sweng.rps.models.remote.LeaderBoardInfo
import ch.epfl.sweng.rps.models.remote.User
import ch.epfl.sweng.rps.models.ui.UserStat
import ch.epfl.sweng.rps.persistence.Cache
import ch.epfl.sweng.rps.persistence.PrivateStorage
import ch.epfl.sweng.rps.persistence.Storage
import ch.epfl.sweng.rps.remote.Env
import ch.epfl.sweng.rps.services.ServiceLocator
import com.google.firebase.ktx.Firebase
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class CacheTests {
    private lateinit var cache: Cache
    private lateinit var cacheWithoutAuth: Cache
    private lateinit var storage: Storage


    @Before
    fun setUp() {
        ServiceLocator.setCurrentEnv(Env.Prod)
        Firebase.initializeForTest()
        cacheWithoutAuth = Cache.initialize(
            InstrumentationRegistry.getInstrumentation().targetContext,
            mockk(relaxed = true)
        )
        cache = Cache.initialize(InstrumentationRegistry.getInstrumentation().targetContext)
        storage = PrivateStorage(InstrumentationRegistry.getInstrumentation().targetContext)
        for (file in Storage.FILES.values()) {
            storage.deleteFile(file)
        }
    }

    @After
    fun tearDown() {
        for (file in Storage.FILES.values()) {
            storage.deleteFile(file)
        }
    }

    @Test
    fun cacheContainsNoDataWhenCreated() {
        assertNull(cache.getUserDetailsFromCache())
        assertTrue(cache.getStatsDataFromCache().isEmpty())
        assertTrue(cache.getLeaderBoardDataFromCache().isEmpty())
    }

    @Test
    fun cacheCorrectlyRetrievesUserDetailsFromStorage() = runBlocking {
        assertNull(cacheWithoutAuth.getUserDetailsFromCache())
        cacheWithoutAuth.setUserDetails(User(uid = "RAND"))
        assertEquals(cacheWithoutAuth.getUserDetailsFromCache()?.uid, "RAND")
    }

    @Test
    fun cacheCorrectlySavesUser() {
        val user = User(username = "USERNAME", uid = "01234", email = "test@test.org")
        cache.setUserDetails(user)
        cache.clearLocalVars()
        assertEquals(cache.getUserDetailsFromCache()!!, user)
    }

    @Test
    fun cacheCorrectlySavesStatsData() {
        assertTrue(cache.getStatsDataFromCache().isEmpty())
        val lst = listOf(
            UserStat(gameId = "1234", date = "14/07/2020", opponents = "Opp", "Best 5", "0"),
            UserStat(gameId = "1244", date = "14/07/1020", opponents = "Opp1", "Best 3", "0"),
            UserStat(gameId = "12134", date = "14/07/3020", opponents = "Opp2", "Best 1", "0"),
        )
        cache.updateStatsData(lst)
        cache.clearLocalVars()
        val result = cache.getStatsDataFromCache()
        assertEquals(result, lst)
    }

    @Test
    fun cacheCorrectlySavesLeaderBoardData() {
        assertTrue(cache.getLeaderBoardDataFromCache().isEmpty())
        val user1 =
            LeaderBoardInfo("jinglun", "1", Uri.parse("https://example.com/"), 100)
        val user2 =
            LeaderBoardInfo("Leonardo", "2", null, 80)
        val user3 =
            LeaderBoardInfo("Adam", "3", null, 60)
        val allPlayers = listOf(user1, user2, user3)
        cache.updateLeaderBoardData(allPlayers)
        cache.clearLocalVars()
        val result = cache.getLeaderBoardDataFromCache()
        assertEquals(result, allPlayers)
    }

    @Test
    fun cacheCorrectlySavesProfileImage() = runBlocking {
        assertNull(cacheWithoutAuth.getUserPicture())
        val btm = createTestBitmap(30, 30, Color.GREEN)
        cacheWithoutAuth.updateUserPicture(btm)
        cache.clearLocalVars()
        assertEquals(cacheWithoutAuth.getUserPicture(), btm)
    }

    @Test
    fun cacheCorrectlyRetrievesProfileImageFromStorage() {
        assertNull(cache.getUserPicture())
        val bitmap = createTestBitmap(20, 20, Color.RED)
        storage.writeBackUserPicture(bitmap)
        cache.clearLocalVars()
        assertNotNull(cache.getUserPicture())
    }

    private fun createTestBitmap(w: Int, h: Int, @ColorInt color: Int?): Bitmap {
        val c = color ?: intArrayOf(
            Color.BLUE, Color.GREEN, Color.RED,
            Color.YELLOW, Color.WHITE
        ).random()
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(c)
        return bitmap
    }
}
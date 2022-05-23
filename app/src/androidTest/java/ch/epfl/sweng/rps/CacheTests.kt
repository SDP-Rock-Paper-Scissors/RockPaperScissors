package ch.epfl.sweng.rps

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.TestUtils.initializeForTest
import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.db.FirebaseRepository
import ch.epfl.sweng.rps.models.remote.LeaderBoardInfo
import ch.epfl.sweng.rps.models.remote.User
import ch.epfl.sweng.rps.models.ui.UserStat
import ch.epfl.sweng.rps.persistence.Cache
import ch.epfl.sweng.rps.persistence.PrivateStorage
import ch.epfl.sweng.rps.persistence.Storage
import ch.epfl.sweng.rps.services.ServiceLocator
import com.google.firebase.ktx.Firebase
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class CacheTests {
    lateinit var cache: Cache
    lateinit var cacheWithoutAuth: Cache
    lateinit var storage: Storage

    @Before
    fun setUp() {
        ServiceLocator.setCurrentEnv(Env.Prod)
        Firebase.initializeForTest()
        cacheWithoutAuth = Cache.createInstance(
            InstrumentationRegistry.getInstrumentation().targetContext,
            mockk<FirebaseRepository>(relaxed = true)
        )
        cache = Cache.createInstance(InstrumentationRegistry.getInstrumentation().targetContext)
        storage = PrivateStorage(InstrumentationRegistry.getInstrumentation().targetContext)
        storage.removeFile(Storage.FILES.STATSDATA)
        storage.removeFile(Storage.FILES.USERINFO)
        storage.removeFile(Storage.FILES.LEADERBOARDDATA)
        storage.removeFile(Storage.FILES.USERPICTURE)
    }

    @After
    fun tearDown() {
        storage.removeFile(Storage.FILES.STATSDATA)
        storage.removeFile(Storage.FILES.USERINFO)
        storage.removeFile(Storage.FILES.LEADERBOARDDATA)
        storage.removeFile(Storage.FILES.USERPICTURE)
    }

    @Test
    fun cacheContainsNoDataWhenCreated() {
        assert(cache.getUserDetails() == null)
        assert(cache.getStatsData(0).isEmpty())
        assert(cache.getLeaderBoardData(0).isEmpty())
    }

    @Test
    fun cacheCorrectlyRetrievesUserDetailsFromStorage() {
        runBlocking {
            assert(cacheWithoutAuth.getUserDetails() == null)
            cacheWithoutAuth.updateUserDetails(User(uid = "RAND"))
            assert(cacheWithoutAuth.getUserDetails()?.uid == "RAND")
        }
    }

    @Test
    fun cacheCorrectlySavesUser() {
        val user: User? = User(username = "USERNAME", uid = "01234", email = "test@test.org")
        cache.updateUserDetails(user)
        assert(cache.getUserDetails()!! == user)
    }

    @Test
    fun cacheCorrectlySavesStatsData() {
        assert(cache.getStatsData(0).isEmpty())
        val lst = listOf<UserStat>(
            UserStat(gameId = "1234", date = "14/07/2020", opponents = "Opp", "Best 5", "0"),
            UserStat(gameId = "1244", date = "14/07/1020", opponents = "Opp1", "Best 3", "0"),
            UserStat(gameId = "12134", date = "14/07/3020", opponents = "Opp2", "Best 1", "0"),
        )
        cache.updateStatsData(lst)
        val result = cache.getStatsData(0)
        assert(result == lst)
    }

    @Test
    fun cacheCorrectlySavesLeaderBoardData() {
        assert(cache.getLeaderBoardData(0).isEmpty())
        val user1 =
            LeaderBoardInfo("jinglun", "1", null, 100)
        val user2 =
            LeaderBoardInfo("Leonardo", "2", null, 80)
        val user3 =
            LeaderBoardInfo("Adam", "3", null, 60)
        val allPlayers = listOf(user1, user2, user3)
        cache.updateLeaderBoardData(allPlayers)
        val result = cache.getLeaderBoardData(0)
        assert(result == allPlayers)
    }

    @Test
    fun cacheCorrectlySavesProfileImage() {
        assert(cacheWithoutAuth.getUserPicture() == null)
        val btm = createTestBitmap(30, 30, null)
        runBlocking {
            cacheWithoutAuth.updateUserPicture(btm)
        }
        assert(cacheWithoutAuth.getUserPicture() == btm)
    }

    @Test
    fun cacheCorrectlyRetrievesProfileImageFromStorage() {
        assert(cache.getUserPicture() == null)
        val bitmap = createTestBitmap(20, 20, Color.RED)
        storage.writeBackUserPicture(bitmap)
        assert(cache.getUserPicture() != null)
    }

    fun createTestBitmap(w: Int, h: Int, @ColorInt color: Int?): Bitmap {
        var color = color
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        if (color == null) {
            val colors = intArrayOf(
                Color.BLUE, Color.GREEN, Color.RED,
                Color.YELLOW, Color.WHITE
            )
            val rgen = Random()
            color = colors[rgen.nextInt(colors.size - 1)]
        }
        canvas.drawColor(color)
        return bitmap
    }
}
package ch.epfl.sweng.rps

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.models.LeaderBoardInfo
import ch.epfl.sweng.rps.models.User
import ch.epfl.sweng.rps.models.UserStat
import ch.epfl.sweng.rps.persistence.Cache
import ch.epfl.sweng.rps.persistence.PrivateStorage
import ch.epfl.sweng.rps.persistence.Storage
import ch.epfl.sweng.rps.services.ServiceLocator
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CacheTests {
    lateinit  var cache :Cache
    @Before
    fun setUp(){
        ServiceLocator.setCurrentEnv(Env.Test)
        cache = Cache.createInstance(InstrumentationRegistry.getInstrumentation().targetContext)
        val storage = PrivateStorage(InstrumentationRegistry.getInstrumentation().targetContext)
        storage.removeFile(Storage.FILES.STATSDATA)
        storage.removeFile(Storage.FILES.USERINFO)
    }
    @Test
    fun cacheContainsNoDataWhenCreated(){
        assert(cache.getUserDetails() == null)
        assert(cache.getStatsData(0).isEmpty())
        assert(cache.getLeaderBoardData().isEmpty())
    }
    @Test
    fun cacheCorrectlySavesUser(){
        val user:User? = User(username = "USERNAME",uid="01234", email = "test@test.org")
        cache.updateUserDetails(user)
        assert(cache.getUserDetails()!!.equals(user))
    }
    @Test
    fun cacheCorrectlySavesStatsData(){
        assert(cache.getStatsData(0).isEmpty())
        val lst = listOf<UserStat>(
            UserStat(gameId = "1234", date="14/07/2020", opponents = "Opp","Best 5", "0"),
            UserStat(gameId = "1244", date="14/07/1020", opponents = "Opp1","Best 3", "0"),
            UserStat(gameId = "12134", date="14/07/3020", opponents = "Opp2","Best 1", "0"),
        )
        cache.updateStatsData(lst)
        val result = cache.getStatsData(0)
        assert(result == lst)
    }

    @Test
    fun cacheCorrectlySavesLeaderBoardData(){
        assert(cache.getLeaderBoardData().isEmpty())
        val user1 =
            LeaderBoardInfo("jinglun", "1", Uri.parse("https://images.unsplash.com/photo-1511367461989-f85a21fda167?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1631&q=80"), 100 )
        val user2 =
            LeaderBoardInfo("Leonardo", "2", Uri.parse("https://images.unsplash.com/photo-1511367461989-f85a21fda167?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1631&q=80"), 80 )
        val user3 =
            LeaderBoardInfo("Adam", "3", Uri.parse("https://images.unsplash.com/photo-1511367461989-f85a21fda167?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1631&q=80"), 60)
        val allPlayers = listOf(user1,user2,user3)
        cache.updateLeaderBoardData(allPlayers)
        val result = cache.getLeaderBoardData()
        assert(result == allPlayers)
    }
}
package ch.epfl.sweng.rps.ui.leaderboard

import androidx.lifecycle.ViewModel
import ch.epfl.sweng.rps.models.remote.LeaderBoardInfo
import ch.epfl.sweng.rps.persistence.Cache
import ch.epfl.sweng.rps.utils.SuspendResult

class LeaderBoardViewModel : ViewModel() {
    private val cache = Cache.getInstance()


    suspend fun getLeaderBoard(position: Int): SuspendResult<List<LeaderBoardInfo>> =
        cache.getLeaderBoardData(position)

}
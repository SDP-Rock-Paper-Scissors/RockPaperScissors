package ch.epfl.sweng.rps.ui.statistics

import androidx.lifecycle.ViewModel
import ch.epfl.sweng.rps.models.ui.UserStat
import ch.epfl.sweng.rps.persistence.Cache
import ch.epfl.sweng.rps.utils.SuspendResult

class StatisticsViewModel : ViewModel() {
    private val cache = Cache.getInstance()

    suspend fun getStats(position: Int): SuspendResult<List<UserStat>> =
        cache.getStatsData(position)

}
package ch.epfl.sweng.rps.ui.leaderboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.epfl.sweng.rps.models.LeaderBoardInfo
import ch.epfl.sweng.rps.models.UserStat
import ch.epfl.sweng.rps.persistence.Cache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LeaderBoardViewModel constructor() : ViewModel(){
    private val cache = Cache.getInstance()!!
    fun getLeaderBoard() : LiveData<List<LeaderBoardInfo>> {
        var livedata = MutableLiveData<List<LeaderBoardInfo>>()
        viewModelScope.launch(Dispatchers.IO) {
            livedata.postValue(cache.getLeaderBoardDataAsync())
        }
        return livedata
    }
}
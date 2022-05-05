package ch.epfl.sweng.rps.ui.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.epfl.sweng.rps.models.UserStat
import ch.epfl.sweng.rps.persistence.Cache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StatisticsViewModel : ViewModel(){
    private val cache = Cache.getInstance()!!
    fun getStats(position:Int) : LiveData<List<UserStat>>{
        var livedata = MutableLiveData<List<UserStat>>()
        viewModelScope.launch(Dispatchers.IO) {
            livedata.postValue(cache.getStatsDataAsync(position))
        }
        return livedata
    }
}
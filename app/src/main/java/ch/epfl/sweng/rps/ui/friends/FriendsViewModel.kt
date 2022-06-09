package ch.epfl.sweng.rps.ui.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.epfl.sweng.rps.models.ui.FriendsInfo
import ch.epfl.sweng.rps.persistence.Cache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendsViewModel : ViewModel(){
    private val cache = Cache.getInstance()!!
    fun getFriends() : LiveData<List<FriendsInfo>> {
        var livedata = MutableLiveData<List<FriendsInfo>>()
        viewModelScope.launch(Dispatchers.IO) {
            livedata.postValue(cache.getFriendsAsync())
        }
        return livedata
    }
}
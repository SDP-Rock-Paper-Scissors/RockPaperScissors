package ch.epfl.sweng.rps.ui.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.epfl.sweng.rps.models.FriendRequestInfo
import ch.epfl.sweng.rps.persistence.Cache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFriendsRequestsModel : ViewModel(){
    private val cache = Cache.getInstance()!!
    fun getFriends() : LiveData<List<FriendRequestInfo>> {
        var livedata = MutableLiveData<List<FriendRequestInfo>>()
        viewModelScope.launch(Dispatchers.IO) {
            livedata.postValue(cache.getFriendReqsAsync())
        }
        return livedata
    }
}
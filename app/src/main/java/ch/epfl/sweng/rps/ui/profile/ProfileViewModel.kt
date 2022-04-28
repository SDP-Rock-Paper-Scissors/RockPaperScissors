package ch.epfl.sweng.rps.ui.profile

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.epfl.sweng.rps.persistence.Cache
import ch.epfl.sweng.rps.services.ServiceLocator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val cache = Cache.getInstance()!!
    fun getProfilePicture() : LiveData<Bitmap> {
        var livedata = MutableLiveData<Bitmap>()
        viewModelScope.launch(Dispatchers.IO) {
            livedata.postValue(cache.getUserPictureAsync())
        }
        return livedata
    }
    fun updateProfilePicture(bitmap: Bitmap){
        viewModelScope.launch(Dispatchers.IO) {
            cache.updateUserPicture(bitmap)
        }
    }
    fun getCachedUserPicture() : Bitmap?{
        return cache.getUserPicture()
    }
}
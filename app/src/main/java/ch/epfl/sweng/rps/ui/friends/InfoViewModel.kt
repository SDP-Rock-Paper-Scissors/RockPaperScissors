package ch.epfl.sweng.rps.ui.friends

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.epfl.sweng.rps.persistence.Cache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InfoViewModel : ViewModel() {
    private val cache = Cache.getInstance()

    suspend fun getProfilePicture(): Bitmap? {
        return cache.getUserPictureAsync()
    }

    fun updateProfilePicture(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            cache.updateUserPicture(bitmap)
        }
    }

    suspend fun getCachedUserPicture(): Bitmap? {
        return withContext(Dispatchers.IO) {
            cache.getUserPicture()
        }
    }
}
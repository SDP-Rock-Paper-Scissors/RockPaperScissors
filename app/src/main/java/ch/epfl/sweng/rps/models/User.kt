package ch.epfl.sweng.rps.models

import android.net.Uri
import android.provider.ContactsContract
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

data class User(
    val username: String? = null,
    val uid: String,
    val gamesHistoryPublic: Boolean = true,
    val friends: List<String>,
    val hasProfilePhoto: Boolean = false,
    val email: String?
) {
    class Field private constructor(val field: String) {
        companion object {
            fun username() = Field("username")
            fun gamesHistoryPublic() = Field("gamesHistoryPublic")
        }
    }
}
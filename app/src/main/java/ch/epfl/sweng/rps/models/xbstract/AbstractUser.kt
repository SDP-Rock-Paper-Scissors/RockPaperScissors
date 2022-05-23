package ch.epfl.sweng.rps.models.xbstract

abstract class AbstractUser(
    open val username: String? = "",
    open val uid: String = "",
    open val games_history_privacy: String = "",
    open val has_profile_photo: Boolean = false,
)
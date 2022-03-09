package ch.epfl.sweng.rps.models

data class User(
    val username: String? = null,
    val uid: String,
    val gamesHistoryPublic: Boolean = true,
    val friends: List<String>,
    val hasProfilePhoto: Boolean = false,
    val email: String?
) {
    class Field private constructor(val value: String) {
        companion object {
            val username get() = Field("username")
            val gamesHistoryPublic get() = Field("gamesHistoryPublic")
        }
    }
}
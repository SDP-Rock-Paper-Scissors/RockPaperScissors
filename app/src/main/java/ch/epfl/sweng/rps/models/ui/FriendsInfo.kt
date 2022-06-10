package ch.epfl.sweng.rps.models.ui

data class FriendsInfo(
    val username : String,
    val uid: String,
    val gamesPlayed : Int,
    val gamesWon : Int,
    val winRate : Double,
    val isOnline : Boolean
    )




package ch.epfl.sweng.rps.models

data class FriendsInfo(
    val username : String,
    val gamesPlayed : Int,
    val gamesWon : Int,
    val winRate : Double,
    val isOnline : Boolean
    )




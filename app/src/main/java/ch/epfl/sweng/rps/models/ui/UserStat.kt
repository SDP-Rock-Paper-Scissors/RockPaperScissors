package ch.epfl.sweng.rps.models.ui

data class UserStat(

    var gameId: String = "",
    var date: String = "",
    var opponents: String = "",
    var gameMode: String = "",
    var userScore: String = "",
    var opponentScore: String = "",
    var outCome: Int = 0,

    )
package ch.epfl.sweng.rps.models

data class RoundStat(
    var index: String = "",
    var date: String = "",
    var userHand: String = "",
    var opponentHand: String = "",
    var outcome: String = ""
)
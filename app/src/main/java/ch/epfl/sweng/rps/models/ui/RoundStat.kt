package ch.epfl.sweng.rps.models.ui

import ch.epfl.sweng.rps.models.remote.Hand
import java.util.*

data class RoundStat(
    val index: Int,
    val date: Date,
    val userHand: Hand,
    val opponentHand: Hand,
    val outcome: Hand.Result
)
package ch.epfl.sweng.rps.models

import com.google.firebase.Timestamp
import java.util.*


/**
 * Represents a game.
 * @property gameId Unique ID of the game
 * @property uid user id.
 * @property players list of players id and information if it is a bot
 * @property mode The time limit, if any, in seconds.
 */
data class Game(
    val gameId: String,
    val uid: String,
    val players: List<Uid>,
    val mode: Mode,
    var roundsList: MutableList<Round>?
) {
    private var uidToScore: MutableMap<String, Int> = mutableMapOf()

    init {
        assert((players.size + 1)== mode.playerCount) { "The number of players doesn't coincide with the gameMode !" }
        initializeScoreList()
    }


    /**
     * Represents a game mode.
     * @property playerCount The number of players in the game, including computers.
     * @property type The type of opponents
     * @property time The time limit, if any, in seconds.
     */
    data class Mode(val playerCount: Int, val type: Type, val time: Int?, val rounds: Int) {
        enum class Type {
            LOCAL, PVP, PC
        }
    }

    data class Uid(
        val uid: String,
        val isComputer: Boolean = false
    )

    private fun initializeScoreList(){
        this.uidToScore = mutableMapOf()
        for (id in players){
            uidToScore[id.uid] = 0
        }
        uidToScore[uid] = 0
    }

    /**
     * After a finished round (at time the hands of each player is known) the round is added.
     */
    fun addRound(uid:String, userHand: Hand, opponentsHands: Map<String, Hand>){
        val roundResult = mutableMapOf<String, Hand>()
        for ((key, value) in opponentsHands.entries){
            roundResult[key] = value
        }
        roundResult[uid] = userHand
        if (roundsList == null){
            roundsList = mutableListOf(Round(roundResult, Timestamp(Date()), "toChangeToRealUid"))
        }
        else{
            roundsList?.add(Round(roundResult, Timestamp(Date()), "toChangeToRealUid"))
        }
    }
}

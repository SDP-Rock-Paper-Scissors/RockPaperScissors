package ch.epfl.sweng.rps.db

import ch.epfl.sweng.rps.models.Game
import ch.epfl.sweng.rps.models.User
import java.text.SimpleDateFormat

sealed class FirebaseHelper {
    companion object {
        fun processUserArguments(vararg pairs: Pair<User.Field, Any>): Map<String, Any> {
            return pairs.associate { t -> t.first.value to t.second }
        }


        fun userFrom(uid: String, name: String?, email: String?): User {
            return User(
                email = email,
                username = name,
                games_history_privacy = User.Privacy.PUBLIC.name,
                has_profile_photo = false,
                uid = uid,
            )
        }

        suspend fun getStatsData (uid: String): MutableList<List<String>> {
            val userGameList = FirebaseRepository().gamesOfUser(uid)
            val statsResult: MutableList<String> = ArrayList()
            val allStatsResult: MutableList<List<String>> = java.util.ArrayList()
            for(userGame in userGameList){
                val game = FirebaseRepository().getGame(userGame.id)
                val opponents = game?.players
                val gameRounds = game?.rounds
                val allRoundScores = gameRounds?.map {it.value.computeScores() }
                val userScore= allRoundScores?.asSequence()?.map { scores ->
                    val max = scores.maxOf { it.points }
                    if (scores.any {it.points == max &&  it.uid == uid && !scores.all { it -> it.points ==max }})
                        1
                    else
                        0

                }?.sum()

                val roundMode = game?.mode?.rounds
                val opponentScore = roundMode?.minus(userScore!!)
                val score = "$userScore - $opponentScore"
                val date = SimpleDateFormat("yyyy-MM-dd").format(game?.timestamp?.toDate())
                statsResult.add(date)
                statsResult.add(opponents.toString())
                statsResult.add(roundMode.toString())
                statsResult.add(score)
                allStatsResult.add(statsResult)
            }
            return allStatsResult


        }


    }
}
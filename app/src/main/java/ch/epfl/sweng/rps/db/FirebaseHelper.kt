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

        suspend fun getStatsData (uid: String, selectMode: Int): MutableList<List<String>> {
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
                //by default 1v1 here
                val opponentScore = roundMode?.minus(userScore!!)
                // should be shown like "3 -2 "
                val score = "$userScore - $opponentScore"
                val date = SimpleDateFormat("yyyy-MM-dd").format(game?.timestamp?.toDate())

                    statsResult.add(date)
                    statsResult.add(opponents.toString())
                    statsResult.add(roundMode.toString())
                    statsResult.add(score)
                //mode filter, selectMode = 0 means by default to fetch all result
                if(roundMode == selectMode || selectMode == 0) {
                    allStatsResult.add(statsResult)
                }
            }
            return allStatsResult


        }

        suspend fun getMatchDetailData(uid: String,gid: String){
            val matchDetail: MutableList<String> = ArrayList()
            val game = FirebaseRepository().getGame(gid)
            val opponent = game?.players
            val handsList = game?.rounds?.map { it.value.hands }
            for (hands in handsList!!){
                matchDetail.add(hands[uid]!!.id.toString())
                matchDetail.add(hands[opponent?.get(0)]!!.id.toString())
                //TODO: how can I get the result of each round?
                // Note： if the user get win, the opponent side should show fail.


            }




        }


    }
}
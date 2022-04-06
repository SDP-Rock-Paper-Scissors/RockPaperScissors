package ch.epfl.sweng.rps.db

import ch.epfl.sweng.rps.models.RoundStat
import ch.epfl.sweng.rps.models.User
import ch.epfl.sweng.rps.models.UserStat
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

        suspend fun getStatsData (selectMode: Int): MutableList<UserStat> {
            val userid = FirebaseRepository().getCurrentUid()
            val userGameList = FirebaseRepository().gamesOfUser(userid)
            val allStatsResult = mutableListOf<UserStat>()
            for(userGame in userGameList){
                val userStat: UserStat? = null
                var opponentId: String? = null
                val game = FirebaseRepository().getGame(userGame.id)
                val players = game?.players
                if (players != null) {
                    for (player in players){
                        if (player!= userid){ opponentId = player}
                    }
                }
                val gameRounds = game?.rounds
                val allRoundScores = gameRounds?.map {it.value.computeScores() }
                val userScore= allRoundScores?.asSequence()?.map { scores ->
                    val max = scores.maxOf { it.points }
                    if (scores.any {it.points == max &&  it.uid == FirebaseRepository().getCurrentUid() && !scores.all { it.points ==max }})
                        1
                    else
                        0

                }?.sum()

                val roundMode = game?.mode?.rounds
                //by default 1v1 here, so just use overall rounds minus his score
                val opponentScore = roundMode?.minus(userScore!!)
                // should be shown like "3 - 2 "
                val score = "$userScore - $opponentScore"
                val date = SimpleDateFormat("yyyy-MM-dd").format(game?.timestamp?.toDate())
                userStat!!.gameId = game!!.id
                userStat.date = date
                // Pass the id temporarily since I am not sure how to get name from id
                userStat.opponents = opponentId.toString()
                userStat.roundMode = roundMode.toString()
                userStat.score = score

                //mode filter, selectMode = 0 means by default to fetch all result
                // todo: map selectMode (best of X) in UI to Game round number
                if(roundMode == selectMode || selectMode == 0) {
                    allStatsResult.add(userStat)
                }
            }
            return allStatsResult


        }

        suspend fun getMatchDetailData(gid: String): MutableList<RoundStat> {
            val userid = FirebaseRepository().getCurrentUid()
            var opponentId: String? = null
            val allDetailsList = mutableListOf<RoundStat>()
            val game = FirebaseRepository().getGame(gid)
            val players = game?.players
            // note: 1 v 1 logic, if we support pvp mode, the table should be iterated to change as well.
            // get opponent user id from player list
            if (players != null) {
                for (player in players){
                    if (player!= userid){ opponentId = player}
                }
            }
            val rounds = game?.rounds
            rounds?.forEach { round ->
                var index = 0
                val roundStat: RoundStat? = null
                val hand = round.value.hands
                val score = round.value.computeScores()
                // id means choice ()
                roundStat!!.userHand = hand[userid]!!.id.toString()
                roundStat!!.opponentHand = hand[opponentId]!!.id.toString()
                when {
                    score[0].uid == userid -> {
                        roundStat.outcome = "win"
                    }
                    score[0].points == 0 -> {
                        roundStat.outcome = "tie"
                    }
                    else -> roundStat.outcome = "lose"
                }
                index ++
                roundStat.index = index.toString()
                allDetailsList.add(roundStat)
            }


        return allDetailsList
        }


    }
}
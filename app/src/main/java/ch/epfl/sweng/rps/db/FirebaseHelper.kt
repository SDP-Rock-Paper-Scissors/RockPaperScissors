package ch.epfl.sweng.rps.db

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

        suspend fun getStatsData (selectMode: Int): MutableList<List<String>> {
            val userGameList = FirebaseRepository().gamesOfUser(FirebaseRepository().getCurrentUid())
            val statsResult: MutableList<String> = ArrayList()
            val allStatsResult: MutableList<List<String>> = java.util.ArrayList()
            for(userGame in userGameList){
                val game = FirebaseRepository().getGame(userGame.id)
                val opponents = game?.players
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
                //by default 1v1 here
                val opponentScore = roundMode?.minus(userScore!!)
                // should be shown like "3 -2 "
                val score = "$userScore - $opponentScore"
                val date = SimpleDateFormat("yyyy-MM-dd").format(game?.timestamp?.toDate())
                    statsResult.add(game!!.id)
                    statsResult.add(date)
                    statsResult.add(opponents.toString())
                    statsResult.add(roundMode.toString())
                    statsResult.add(score)
                //mode filter, selectMode = 0 means by default to fetch all result
                // todo: map selectMode (best of X) in UI to Game round number
                if(roundMode == selectMode || selectMode == 0) {
                    allStatsResult.add(statsResult)
                }
            }
            return allStatsResult


        }

        suspend fun getMatchDetailData(gid: String): MutableList<List<String>> {
            val userid = FirebaseRepository().getCurrentUid()
            var opponentId: String? = null
            val matchDetail: MutableList<String> = ArrayList()
            // return @matchDetail List: [user hand, opponent hand, round outcome, index]
            val allDetailsList: MutableList<List<String>> = java.util.ArrayList()
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
                val hand = round.value.hands
                val score = round.value.computeScores()
                // id means choice ()
                matchDetail.add(hand[userid]?.id.toString())
                matchDetail.add(hand[opponentId]?.id.toString())
                when {
                    score[0].uid == userid -> {
                        matchDetail.add("win")
                    }
                    score[0].points == 0 -> {
                        matchDetail.add("tie")
                    }
                    else -> matchDetail.add("lose")
                }
                index ++
                matchDetail.add(index.toString())
            }
            allDetailsList.add(matchDetail)

        return allDetailsList
        }


    }
}
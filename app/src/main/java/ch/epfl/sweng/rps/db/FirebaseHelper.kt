package ch.epfl.sweng.rps.db

import android.R
import android.net.Uri
import ch.epfl.sweng.rps.models.*
import ch.epfl.sweng.rps.services.ServiceLocator
import java.text.SimpleDateFormat
import java.util.*



object FirebaseHelper {
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

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

    suspend fun getStatsData(selectMode: Int): List<UserStat> {
        val firebaseRepository = ServiceLocator.getInstance().repository
        val userid = firebaseRepository.rawCurrentUid() ?: return emptyList()

        val userGameList = firebaseRepository.gamesOfUser(userid)

        val allStatsResult = mutableListOf<UserStat>()
        for (userGame in userGameList) {
            val userStat = UserStat()
            val players = userGame.players
            val opponents = mutableListOf<String>()
            for (p in players) {
                if (p != userid) {
                    val user = firebaseRepository.getUser(p)

                    if (user != null)
                        opponents.add(user.username ?: p)
                }
            }

            val gameRounds = userGame.rounds
            val allRoundScores = gameRounds.map { it.value.computeScores() }

            val userScore = allRoundScores.asSequence().map { scores ->
                val max = scores.maxOf { it.points }
                if (scores.any { it.points == max && it.uid == userid && !scores.all { it.points == max } })
                    1
                else
                    0

            }.sum()

            val roundMode = userGame.gameMode.rounds
            //by default 1v1 here, so just use overall rounds minus his score
            val opponentScore = roundMode.minus(userScore)
            // should be shown like "3 - 2 "
            val score = "$userScore - $opponentScore"
            val date =
                dateFormat.format(userGame.timestamp.toDate())
            userStat.gameId = userGame.id
            userStat.date = date
            // Pass the id temporarily since I am not sure how to get name from id
            userStat.opponents = opponents.joinToString(",") { it }
            userStat.roundMode = roundMode.toString()
            userStat.score = score

            //mode filter, selectMode = 0 means by default to fetch all result
            // todo: map selectMode (best of X) in UI to Game round number
            if (roundMode == selectMode || selectMode == 0) {
                allStatsResult.add(userStat)
            }
        }
        return allStatsResult


    }

    suspend fun getMatchDetailData(gid: String): List<RoundStat> {
        val repo = ServiceLocator.getInstance().repository
        val userid = repo.rawCurrentUid()

        val game = repo.getGame(gid) ?: throw Exception("Game not found")
        // note: 1 v 1 db, if we support pvp mode, the table should be iterated to change as well.
        // get opponent user id from player list
        val opponentId: String = game.players.first { it != userid }
        val allDetailsList = game.rounds.entries.mapIndexed { i, round ->
            val hand = round.value.hands
            val score = round.value.computeScores()
            // id means choice ()
            val userHand = hand[userid]!!
            val opponentHand = hand[opponentId]!!
            val outcome = when {
                score[0].uid == userid -> Hand.Result.WIN
                score[0].points == 0 -> Hand.Result.TIE
                else -> Hand.Result.LOSS
            }

            RoundStat(
                outcome = outcome,
                index = i,
                opponentHand = opponentHand,
                userHand = userHand,
                date = game.timestamp.toDate()
            )
        }

        return allDetailsList
    }


    suspend fun getLeaderBoard(selectMode: Int): List<LeaderBoardInfo> {
        val repo = ServiceLocator.getInstance().repository
        val scoreMode: String = when (selectMode) {
            0 -> "RPSScore"
            else -> "TTTScore"
        }
        val scores = repo.getLeaderBoardScore(scoreMode)
        val allPlayers = mutableListOf<LeaderBoardInfo>()
        for (score in scores){
            val leaderBoardInfo = LeaderBoardInfo()
            leaderBoardInfo.uid = score.uid!!
            leaderBoardInfo.point = when (selectMode) {
                0 -> score.RPSScore!!
                else -> score.TTTScore!!
            }
            // The *load* function only support "android.net.Uri" but not "java.net.URI" package
            leaderBoardInfo.userProfilePictureUrl = repo.getUserProfilePictureUrl(score.uid)?.let { Uri.parse(it.toString()) }
            if(leaderBoardInfo.userProfilePictureUrl == null){
                leaderBoardInfo.userProfilePictureUrl = Uri.parse("android.resource://ch.epfl.sweng.rps/" + R.drawable.sym_def_app_icon)

            }
            leaderBoardInfo.username = repo.getUser(score.uid)!!.username!!
            allPlayers.add(leaderBoardInfo)
        }


        return allPlayers
    }

    suspend fun getFriends(): List<FriendsInfo> {
        val fbRepo = ServiceLocator.getInstance().repository
        val friends = fbRepo.getFriends()
        val friendList = mutableListOf<FriendsInfo>()

        for (friend in friends) {
            val user = fbRepo.getUser(friend)?:continue
            val userStats = fbRepo.statsOfUser(friend)
            val friendsInfo = FriendsInfo(
                username = user.username?:"UsernameEmpty",
                gamesPlayed = userStats.total_games,
                gamesWon = userStats.wins,
                winRate = userStats.winRate,
                isOnline = true)

            friendList.add(friendsInfo)
        }
        return friendList
    }

    suspend fun getFriendReqs(): List<FriendRequestInfo> {
        val fbRepo = ServiceLocator.getInstance().repository
        val friendRequest = fbRepo.listFriendRequests()
        val reqList = mutableListOf<FriendRequestInfo>()
        val uid = fbRepo.rawCurrentUid()

        for (req in friendRequest) {
            if (req.from != uid) {
                val user = fbRepo.getUser(req.from) ?: continue
                val friendsReq = FriendRequestInfo(
                    username = user.username ?: "UsernameEmpty",
                    uid = req.from
                )
                reqList.add(friendsReq)
            }
        }
        return reqList
    }

    }






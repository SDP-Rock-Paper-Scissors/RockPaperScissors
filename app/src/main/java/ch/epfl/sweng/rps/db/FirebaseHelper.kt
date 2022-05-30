package ch.epfl.sweng.rps.remote


import android.net.Uri
import ch.epfl.sweng.rps.R
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
            val gameRounds = userGame.rounds
            val opponents = mutableListOf<String>()
            val roundMode = userGame.gameMode.rounds
            val gameMode = userGame.gameMode.edition.id
            val gameModeName: String
            val gameModeID: Int
            val date = dateFormat.format(userGame.timestamp.toDate())
            val allRoundScores =
                gameRounds.map { it.value.computeScores() }.filter { it.isNotEmpty() }
            val userScore = allRoundScores.sumOf { scores ->
                val max = scores.maxOf { it.points }

                if (scores.any { it.points == max && it.uid == userid && !scores.all { score -> score.points == max } })
                    1L
                else
                    0L

            }
            val opponentScore = roundMode.minus(userScore)
            val score = userScore - opponentScore
            val outcome: Int = when {
                score < 0 -> -1
                score == 0L -> 0
                else -> 1
            }

            for (p in players) {
                if (p != userid) {
                    val user = firebaseRepository.getUser(p)

                    if (user != null)
                        opponents.add(user.username ?: p)
                }
            }

            if (gameMode == "ttt") {
                gameModeName = "Tic-Tac-Toe"
                gameModeID = 2
            } else {
                gameModeName = "Rock-Paper-Scissor"
                gameModeID = 1
            }

            userStat.gameId = userGame.id
            userStat.date = date
            userStat.opponents = opponents.joinToString(",") { it }
            userStat.gameMode = gameModeName
            userStat.userScore = userScore.toString()
            userStat.opponentScore = opponentScore.toString()
            userStat.outCome = outcome


            //  mode filter, selectMode = 0 means by default to fetch all result
            if (gameModeID == selectMode || selectMode == 0) {
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
        for (score in scores) {
            val leaderBoardInfo = LeaderBoardInfo()
            leaderBoardInfo.uid = score.uid!!
            leaderBoardInfo.point = when (selectMode) {
                0 -> score.RPSScore!!
                else -> score.TTTScore!!
            }
            // The *load* function only support "android.net.Uri" but not "java.net.URI" package
            leaderBoardInfo.userProfilePictureUrl =
                repo.getUserProfilePictureUrl(score.uid)?.let { Uri.parse(it.toString()) }
            if (leaderBoardInfo.userProfilePictureUrl == null) {
                leaderBoardInfo.userProfilePictureUrl =

                    Uri.parse("android.resource://ch.epfl.sweng.rps/" + R.drawable.profile_img)

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




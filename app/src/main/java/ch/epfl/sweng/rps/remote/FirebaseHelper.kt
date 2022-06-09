package ch.epfl.sweng.rps.remote


import android.net.Uri
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.models.*
import ch.epfl.sweng.rps.models.remote.FriendRequest
import ch.epfl.sweng.rps.models.remote.Hand
import ch.epfl.sweng.rps.models.remote.LeaderBoardInfo
import ch.epfl.sweng.rps.models.remote.User
import ch.epfl.sweng.rps.models.ui.FriendRequestInfo
import ch.epfl.sweng.rps.models.ui.FriendsInfo
import ch.epfl.sweng.rps.models.ui.RoundStat
import ch.epfl.sweng.rps.models.ui.UserStat
import ch.epfl.sweng.rps.services.ServiceLocator
import ch.epfl.sweng.rps.utils.Option
import java.text.SimpleDateFormat
import java.util.*


/**
 *  This class is a helper class for to transform the data from the remote database
 *  to a more usable format.
 */
object FirebaseHelper {
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

    /**
     * This function transforms a list of provided [Pair] of [User.Field] and [T]
     * to a map of [String] and [T] where the key is the [User.Field] value and the value is the [T].
     */
    fun <T : Any> processUserArguments(vararg pairs: Pair<User.Field, T>): Map<String, T> {
        return pairs.associate { t -> t.first.value to t.second }
    }

    /**
     * This function creates a [User] from the provided parameters.
     */
    fun userFrom(uid: String, name: String?, email: String?): User {
        return User(
            email = email,
            username = name,
            games_history_privacy = User.Privacy.PUBLIC.name,
            has_profile_photo = false,
            uid = uid,
        )
    }

    /**
     * This function returns stats for all users.
     */
    suspend fun getStatsData(selectMode: Int): List<UserStat> {
        val firebaseRepository = ServiceLocator.getInstance().repository
        val userid = firebaseRepository.rawCurrentUid() ?: return emptyList()
        val userGameList = firebaseRepository.games.gamesOfUser(userid)
        val allStatsResult = mutableListOf<UserStat>()

        // We cache the users to avoid multiple calls to the database
        // We use options to differentiate between the case where the user is not found
        // and the case where the user hasn't been cached yet
        val users = mutableMapOf<String, Option<User>>()
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
                    val user =
                        users.getOrPut(p) { Option.fromNullable(firebaseRepository.getUser(p).getOrThrow()) }

                    if (user is Option.Some)
                        opponents.add(user.value.username ?: p)
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

    /**
     * This function returns the details of a game.
     */
    suspend fun getMatchDetailData(gid: String): List<RoundStat> {
        val repo = ServiceLocator.getInstance().repository
        val userid = repo.rawCurrentUid()

        val game = repo.games.getGame(gid) ?: throw Exception("Game not found")
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
                score[0].uid == userid -> Hand.Outcome.WIN
                score[0].points == 0 -> Hand.Outcome.TIE
                else -> Hand.Outcome.LOSS
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

    /**
     * This function returns the leaderboard.
     */
    suspend fun getLeaderBoard(selectMode: Int): List<LeaderBoardInfo> {
        val repo = ServiceLocator.getInstance().repository
        val scoreMode: String = when (selectMode) {
            0 -> "RPSScore"
            else -> "TTTScore"
        }
        val scores = repo.games.getLeaderBoardScore(scoreMode)
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
                repo.getUserProfilePictureUrl(score.uid).asData?.value.let { Uri.parse(it.toString()) }
            if (leaderBoardInfo.userProfilePictureUrl == null) {
                leaderBoardInfo.userProfilePictureUrl =

                    Uri.parse("android.resource://ch.epfl.sweng.rps/" + R.drawable.profile_img)

            }
            leaderBoardInfo.username = repo.getUser(score.uid).asData?.value?.username ?: "Unknown"
            allPlayers.add(leaderBoardInfo)
        }
        return allPlayers
    }
    /**
     * This function returns the your friends.
     */
    suspend fun getFriends(): List<FriendsInfo> {
        val fbRepo = ServiceLocator.getInstance().repository
        val friends = fbRepo.friends.getFriends()
        val friendList = mutableListOf<FriendsInfo>()

        for (friend in friends) {
            val user = fbRepo.getUser(friend)
            val userStats = fbRepo.games.statsOfUser(friend)
            val friendsInfo = FriendsInfo(
                username = user.asData?.value?.username?: "UsernameEmpty",
                gamesPlayed = userStats.total_games,
                gamesWon = userStats.wins,
                winRate = userStats.winRate,
                isOnline = true)

            friendList.add(friendsInfo)
        }
        return friendList
    }
    /**
     * This function returns your friend requests.
     */
    suspend fun getFriendReqs(): List<FriendRequestInfo> {
        val fbRepo = ServiceLocator.getInstance().repository
        val friendRequest = fbRepo.friends.listFriendRequests()
        val reqList = mutableListOf<FriendRequestInfo>()
        val uid = fbRepo.rawCurrentUid()

        for (req in friendRequest) {
            if (req.from != uid && req.status == FriendRequest.Status.PENDING) {
                val user = fbRepo.getUser(req.from)
                val friendsReq = FriendRequestInfo(
                    username = user.asData?.value?.username?: "UsernameEmpty",
                    uid = req.from
                )
                reqList.add(friendsReq)
            }
        }
        return reqList
    }

}




package com.github.bhvrgav.myfriendspage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private lateinit var newRecyclerView : RecyclerView
private lateinit var newArrayList: ArrayList<FriendsInfo>
lateinit var onlineImage : Array<Int>
lateinit var username : Array<String>
lateinit var gamesPlayed : Array<String>
lateinit var gamesWon : Array<String>
lateinit var winRate : Array<String>

class FriendsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friends_main)

        onlineImage = arrayOf(
            R.drawable.online_vector,
            R.drawable.online_vector,
            R.drawable.online_vector,
            R.drawable.online_vector,
            R.drawable.online_vector,
            R.drawable.offline_vector,
            R.drawable.offline_vector,
            R.drawable.offline_vector,
            R.drawable.offline_vector,
            R.drawable.offline_vector
        )
        username = arrayOf(
            "RPSKing88",
            "UrTrash",
            "Meliodas19",
            "Narut0",
            "RockFirst",
            "JustGary",
            "GreenSerpentXX",
            "UrMom",
            "GameMaster420",
            "InsomniaxGamer"
        )
        gamesPlayed = arrayOf(
            "Games Played: 120",
            "Games Played: 220",
            "Games Played: 86",
            "Games Played: 14",
            "Games Played: 455",
            "Games Played: 141",
            "Games Played: 63",
            "Games Played: 90",
            "Games Played: 312",
            "Games Played: 166"
        )

        gamesWon = arrayOf(
            "Games Won: 76",
            "Games Won: 110",
            "Games Won: 32",
            "Games Won: 12",
            "Games Won: 343",
            "Games Won: 118",
            "Games Won: 28",
            "Games Won: 12",
            "Games Won: 211",
            "Games Won: 65"
        )

        winRate = arrayOf(
            "Win Rate: 63.3%",
            "Win Rate: 50.0%",
            "Win Rate: 37.2%",
            "Win Rate: 85.7%",
            "Win Rate: 75.4%",
            "Win Rate: 83.7%",
            "Win Rate: 44.4%",
            "Win Rate: 13.3%",
            "Win Rate: 67.6%",
            "Win Rate: 39.2%"
        )

        newRecyclerView = findViewById(R.id.friendListRecyclerView)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)

        newArrayList = arrayListOf<FriendsInfo>()
        getFriendsInfo()
    }

    private fun getFriendsInfo() {
        for (i in onlineImage.indices){
            val friendsInfo = FriendsInfo(username[i],gamesPlayed[i],gamesWon[i],winRate[i],onlineImage[i])
            newArrayList.add(friendsInfo)
        }

        newRecyclerView.adapter = friendlistAdapter(newArrayList)
    }

}
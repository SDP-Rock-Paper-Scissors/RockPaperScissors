package com.github.bhvrgav.myfriendspage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class friendlistAdapter(private val friendList : ArrayList<FriendsInfo>) : RecyclerView.Adapter<friendlistAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.friend_card, parent, false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = friendList[position]
        holder.onlineImage.setImageResource(currentItem.onlineImage)
        holder.userName.text = currentItem.username
        holder.gamesPlayed.text = currentItem.gamesPlayed
        holder.gamesWon.text = currentItem.gamesWon
        holder.winRate.text = currentItem.winRate

    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val onlineImage : ImageView = itemView.findViewById(R.id.onlineImage)
        val userName : TextView = itemView.findViewById(R.id.friendName)
        val gamesPlayed : TextView = itemView.findViewById(R.id.gamesPlayedText)
        val gamesWon : TextView = itemView.findViewById(R.id.gamesWonText)
        val winRate : TextView = itemView.findViewById(R.id.winRateText)
    }
}
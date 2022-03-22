package ch.epfl.sweng.rps

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FriendListAdapter(private val friends : List<FriendsInfo>) : RecyclerView.Adapter<FriendListAdapter.CardViewHolder>(){


    class CardViewHolder(val view : View) : RecyclerView.ViewHolder(view) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
         return CardViewHolder(
             LayoutInflater.from(parent.context).inflate(R.layout.friend_card2, parent, false)
         )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val friend = friends[position]

        holder.view.findViewById<TextView>(R.id.friendName).text = friend.username
        holder.view.findViewById<TextView>(R.id.winRateText).text = "Win Rate: " + friend.winRate + "%"
        holder.view.findViewById<TextView>(R.id.gamesPlayedText).text = "Games Played: " + friend.gamesPlayed
        holder.view.findViewById<TextView>(R.id.gamesWonText).text = "Games Won: " + friend.gamesWon
        holder.view.findViewById<ImageView>(R.id.onlineImage).visibility = if(friend.isOnline) View.VISIBLE else View.INVISIBLE
        holder.view.findViewById<ImageView>(R.id.offlineImage).visibility = if(friend.isOnline) View.INVISIBLE else View.VISIBLE


    }

    override fun getItemCount(): Int {
        return friends.size
    }
}
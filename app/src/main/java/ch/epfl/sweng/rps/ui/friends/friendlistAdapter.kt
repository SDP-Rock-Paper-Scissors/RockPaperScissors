package ch.epfl.sweng.rps.ui.friends

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.models.ui.FriendsInfo

class FriendListAdapter(
    private val friends: List<FriendsInfo>,
    private val listener: OnButtonClickListener
) :
    RecyclerView.Adapter<FriendListAdapter.CardViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.friend_card2, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val friend = friends[position]

        holder.friendName.text = friend.username
        holder.winRate.text = "Win Rate: " + friend.winRate + "%"
        holder.gamesPlayed.text = "Games Played: " + friend.gamesPlayed
        holder.gamesWon.text = "Games Won: " + friend.gamesWon
        holder.onlineImage.visibility = if (friend.isOnline) View.VISIBLE else View.INVISIBLE
        holder.offlineImage.visibility = if (friend.isOnline) View.INVISIBLE else View.VISIBLE

    }

    override fun getItemCount(): Int {
        return friends.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val friendName: TextView = itemView.findViewById(R.id.friendName)
        val winRate: TextView = itemView.findViewById(R.id.winRateText)
        val gamesPlayed: TextView = itemView.findViewById(R.id.gamesPlayedText)
        val gamesWon: TextView = itemView.findViewById(R.id.gamesWonText)
        val onlineImage: ImageView = itemView.findViewById(R.id.onlineImage)
        val offlineImage: ImageView = itemView.findViewById(R.id.offlineImage)
        private val playButton: ImageButton = itemView.findViewById(R.id.playButton)
        private val infoButton: ImageButton = itemView.findViewById(R.id.infoButton)

        init {
            playButton.setOnClickListener(this)
            infoButton.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onButtonClick(position, friends, view)
            }
        }
    }

    interface OnButtonClickListener {
        fun onButtonClick(position: Int, friends: List<FriendsInfo>, view: View)
    }
}
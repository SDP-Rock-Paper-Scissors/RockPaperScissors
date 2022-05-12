package ch.epfl.sweng.rps

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.epfl.sweng.rps.models.FriendRequestInfo
import ch.epfl.sweng.rps.models.FriendsInfo

class RequestListAdapter(
    private val requests : List<FriendRequestInfo>,
    private val listener : OnButtonClickListener
) :
    RecyclerView.Adapter<RequestListAdapter.CardViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
         return CardViewHolder(
             LayoutInflater.from(parent.context).inflate(R.layout.request_card, parent, false)
         )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val req = requests[position]

        holder.reqUserName.text = req.username
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener{
        val reqUserName : TextView = itemView.findViewById(R.id.reqUserName)
        val acceptBtn : ImageButton = itemView.findViewById(R.id.acceptButton)
        val rejectBtn : ImageButton = itemView.findViewById(R.id.rejectButton)

        init {
            acceptBtn.setOnClickListener(this)
            rejectBtn.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onButtonClick(position, requests, view)
            }
        }
    }

    interface OnButtonClickListener {
        fun onButtonClick(position: Int, friends: List<FriendRequestInfo>, view: View)
    }

    override fun getItemCount(): Int {
        return requests.size
    }
}
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.models.LeaderBoardInfo
import coil.load
import kotlinx.android.synthetic.main.lb_player_list.view.*

class LeaderBoardPlayerAdapter : RecyclerView.Adapter<LeaderBoardPlayerAdapter.PlayerViewHolder>() {

    private var players: MutableList<LeaderBoardInfo> = mutableListOf()

    override fun getItemCount(): Int = players.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lb_player_list, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(players[position], position)
    }

    fun addPlayers(players: List<LeaderBoardInfo>) {
        this.players.apply {
            clear()
            addAll(players)
        }
        notifyDataSetChanged()
    }

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(player: LeaderBoardInfo, position: Int) {
            itemView.tv_position.text = (position + 1).toString()
            itemView.tv_name.text = player.username
            itemView.tv_score.text = player.point.toString()
            itemView.iv_photo.load(player.userProfilePictureUrl)
        }
    }
}
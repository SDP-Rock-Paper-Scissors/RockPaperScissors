package ch.epfl.sweng.rps.ui.statistics

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.models.ui.UserStat
import coil.load


class StatsItemAdapter(private val fragmentManager: FragmentManager) :
    RecyclerView.Adapter<StatsItemAdapter.ItemViewHolder>() {

    private var statsList: MutableList<UserStat> = mutableListOf()

    override fun getItemCount(): Int = statsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.stats_card, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(statsList[position])
        holder.itemView.findViewById<CardView>(R.id.stats_card).isClickable
        holder.itemView.setOnClickListener {
            val matchDetailFragment = MatchDetailsFragment()
            val bundle = Bundle()
            val transaction = fragmentManager.beginTransaction()
            bundle.putString("uuid", statsList[position].gameId)
            matchDetailFragment.arguments = bundle
            transaction.replace(R.id.fragment_statistics, matchDetailFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addStats(stat: List<UserStat>) {
        this.statsList.apply {
            clear()
            addAll(stat)
        }
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(stat: UserStat) {

            itemView.findViewById<TextView>(R.id.userScore).text = stat.userScore
            itemView.findViewById<TextView>(R.id.match_date).text = stat.date
            itemView.findViewById<TextView>(R.id.opponentName_StatsPage).text = stat.opponents
            itemView.findViewById<TextView>(R.id.opponentScore).text = stat.opponentScore
            itemView.findViewById<TextView>(R.id.match_mode).text = stat.gameMode
            val lossURI = Uri.parse("android.resource://ch.epfl.sweng.rps/" + R.drawable.loss)
            val winURI = Uri.parse("android.resource://ch.epfl.sweng.rps/" + R.drawable.win)
            val tieURI = Uri.parse("android.resource://ch.epfl.sweng.rps/" + R.drawable.tie)

            val outcomeURI = when (stat.outCome) {
                -1 -> lossURI
                1 -> winURI
                else -> tieURI
            }

            itemView.findViewById<ImageView>(R.id.match_outcome_img).load(outcomeURI)
        }
    }
}
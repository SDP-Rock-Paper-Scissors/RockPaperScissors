package ch.epfl.sweng.rps.ui.leaderboard

import LeaderBoardPlayerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.databinding.FragmentLeaderboardBinding
import ch.epfl.sweng.rps.models.LeaderBoardInfo
import ch.epfl.sweng.rps.persistence.Cache
import coil.load


class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private lateinit var cache: Cache


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        cache = Cache.getInstance()!!
        val leaderBoardRecyclerView = itemView.findViewById<RecyclerView>(R.id.leaderboard_recycler_view)
        val model:LeaderBoardViewModel by viewModels()

        leaderBoardRecyclerView?.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = LeaderBoardPlayerAdapter()
            setHasFixedSize(true)

        }

        model.getLeaderBoard().observe(viewLifecycleOwner) { leaderboardData ->
            loadPlayersUI(
                itemView, leaderboardData
            )
        }
    }



    private fun loadPlayersUI(itemView: View, players: List<LeaderBoardInfo>){
        val champions = players.take(3)
        showPlayersPosition(itemView, players)
        showChampions(itemView,champions)

    }

    private fun showChampions(itemView: View, championPlayers: List<LeaderBoardInfo>) {

        itemView.findViewById<ImageView>(R.id.iv_champion1).load(championPlayers[0].userProfilePictureUrl)
        itemView.findViewById<ImageView>(R.id.iv_champion2).load(championPlayers[1].userProfilePictureUrl)
        itemView.findViewById<ImageView>(R.id.iv_champion3).load(championPlayers[2].userProfilePictureUrl)

    }

    private fun showPlayersPosition(
        itemView: View,
        players: List<LeaderBoardInfo>
    ) {
        val adapter = itemView.findViewById<RecyclerView>(R.id.leaderboard_recycler_view).adapter as LeaderBoardPlayerAdapter
        adapter.addPlayers(players)

    }

    private fun getGameModes(): Array<String> {

        return arrayOf(
            "Mode Filter",
            "Rock-Paper-Scissor",
            "Tic-Tac-Toe",
        )

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package ch.epfl.sweng.rps.ui.leaderboard

import LeaderBoardPlayerAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.databinding.FragmentLeaderboardBinding
import ch.epfl.sweng.rps.db.FirebaseHelper.getLeaderBoard
import ch.epfl.sweng.rps.models.LeaderBoardInfo
import ch.epfl.sweng.rps.persistence.Cache
import coil.load
import kotlinx.android.synthetic.main.content_scrolling.*
import kotlinx.coroutines.launch


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
        val model:LeaderBoardViewModel by viewModels()
        recycler_view.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = LeaderBoardPlayerAdapter()
            setHasFixedSize(true)

        }

        model.getLeaderBoard().observe(viewLifecycleOwner) { leaderboardData ->
            loadPlayersUI(
                leaderboardData
            )
        }
    }



    private fun loadPlayersUI(players: List<LeaderBoardInfo>){
        val champions = players.take(3)
        showPlayersPosition(players)
        showChampions(champions)

    }

    private fun showChampions(championPlayers: List<LeaderBoardInfo>) {

        iv_champion1.load(championPlayers[0].userProfilePictureUrl)
        iv_champion2.load(championPlayers[1].userProfilePictureUrl)
        iv_champion3.load(championPlayers[2].userProfilePictureUrl)

    }

    private fun showPlayersPosition(players: List<LeaderBoardInfo>) {
        val adapter = recycler_view.adapter as LeaderBoardPlayerAdapter
        adapter.addPlayers(players)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
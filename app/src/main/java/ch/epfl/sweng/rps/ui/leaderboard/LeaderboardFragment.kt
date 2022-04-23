package ch.epfl.sweng.rps.ui.leaderboard

import LeaderBoardPlayerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.databinding.FragmentLeaderboardBinding
import ch.epfl.sweng.rps.models.User
import kotlinx.android.synthetic.main.content_scrolling.*


class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        recycler_view.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = LeaderBoardPlayerAdapter()
            setHasFixedSize(true)

        }
        //loadPlayersUI(loadLeaderBoard())
    }

    

    private fun loadPlayersUI(players: List<User>){
        val champions = players.take(3)
        showPlayersPosition(players)
        showChampions(champions)

    }

    private fun showChampions(championPlayers: List<User>) {
        TODO("Load the user pics of top 3")
        //iv_champion1.loadImg(championPlayers[0].photo)
        //iv_champion2.loadImg(championPlayers[1].photo)
        //iv_champion3.loadImg(championPlayers[2].photo)

    }

    private fun showPlayersPosition(players: List<User>) {
        val adapter = recycler_view.adapter as LeaderBoardPlayerAdapter
        adapter.addPlayers(players)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
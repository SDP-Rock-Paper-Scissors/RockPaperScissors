package ch.epfl.sweng.rps.ui.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.databinding.FragmentLeaderboardBinding
import ch.epfl.sweng.rps.models.remote.LeaderBoardInfo
import ch.epfl.sweng.rps.persistence.Cache
import coil.load


class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private lateinit var cache: Cache


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val newView = inflater.inflate(R.layout.fragment_leaderboard, container, false)
        val modeSpinner = newView.findViewById(R.id.modeSelect_leaderboard) as Spinner
        val modes = getGameModes()
        val adapter =
            ArrayAdapter(this.requireActivity(), android.R.layout.simple_spinner_item, modes)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        modeSpinner.adapter = adapter
        return newView
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        cache = Cache.getInstance()!!
        val leaderBoardRecyclerView =
            itemView.findViewById<RecyclerView>(R.id.leaderboard_recycler_view)
        val modeSpinner = itemView.findViewById(R.id.modeSelect_leaderboard) as Spinner
        val model: LeaderBoardViewModel by viewModels()
        modeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                leaderBoardRecyclerView.removeAllViews()
                leaderBoardRecyclerView?.apply {
                    // set a LinearLayoutManager to handle Android
                    // RecyclerView behavior
                    layoutManager = LinearLayoutManager(activity)
                    // set the custom adapter to the RecyclerView
                    adapter = LeaderBoardPlayerAdapter()
                    setHasFixedSize(true)
                    model.getLeaderBoard(position).observe(viewLifecycleOwner) { leaderboardData ->
                        loadPlayersUI(
                            itemView, leaderboardData
                        )
                    }

                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // do nothing
            }


        }

    }


    private fun loadPlayersUI(itemView: View, players: List<LeaderBoardInfo>) {
        if(players.size >= 3) {
            val champions = players.take(3)
            showChampions(itemView, champions)
        }
        showPlayersPosition(itemView, players)

    }

    private fun showChampions(itemView: View, championPlayers: List<LeaderBoardInfo>) {

        itemView.findViewById<ImageView>(R.id.iv_champion1)
            .load(championPlayers[0].userProfilePictureUrl)
        itemView.findViewById<ImageView>(R.id.iv_champion2)
            .load(championPlayers[1].userProfilePictureUrl)
        itemView.findViewById<ImageView>(R.id.iv_champion3)
            .load(championPlayers[2].userProfilePictureUrl)

    }

    private fun showPlayersPosition(
        itemView: View,
        players: List<LeaderBoardInfo>
    ) {
        val adapter =
            itemView.findViewById<RecyclerView>(R.id.leaderboard_recycler_view).adapter as LeaderBoardPlayerAdapter
        adapter.addPlayers(players)

    }

    private fun getGameModes(): Array<String> {

        return arrayOf(
            "Rock-Paper-Scissor",
            "Tic-Tac-Toe",
        )

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
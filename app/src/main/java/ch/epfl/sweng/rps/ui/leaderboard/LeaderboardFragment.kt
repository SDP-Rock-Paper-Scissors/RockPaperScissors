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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.databinding.FragmentLeaderboardBinding
import ch.epfl.sweng.rps.models.remote.LeaderBoardInfo
import ch.epfl.sweng.rps.persistence.Cache
import ch.epfl.sweng.rps.utils.SuspendResult
import coil.load
import kotlinx.coroutines.launch


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
                    lifecycleScope.launch {
                        model.getLeaderBoard(position).whenIs(
                            success = { loadPlayersUI(itemView, it.value) },
                            failure = SuspendResult.showSnackbar(requireContext(), requireView()) {}
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
        val champions = players.take(3)
        showPlayersPosition(itemView, players)
        showChampions(itemView, champions)

    }

    private fun showChampions(itemView: View, championPlayers: List<LeaderBoardInfo>) {
        val images = listOf<ImageView>(
            itemView.findViewById(R.id.iv_champion1),
            itemView.findViewById(R.id.iv_champion2),
            itemView.findViewById(R.id.iv_champion3)
        )
        for ((i, c) in championPlayers.withIndex()) {
            images[i].load(c.userProfilePictureUrl)
        }
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
package ch.epfl.sweng.rps.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.databinding.FragmentHomeBinding
import ch.epfl.sweng.rps.models.RandomPlayer
import ch.epfl.sweng.rps.models.remote.Hand


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val matchViewModel: MatchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setNavigationOnButton(_binding!!.buttonTikTacToe, R.id.ticTacToeChoiceFragment)

        binding.apply {
            buttonPlay1GamesOffline.setOnClickListener { playNRoundsWithComputer(1) }
            buttonPlay5GamesOffline.setOnClickListener { playNRoundsWithComputer(5) }

            buttonPlay1GamesOnline.setOnClickListener { playOnlineGame(1) }
            buttonPlay5GamesOnline.setOnClickListener { playOnlineGame(5) }
        }

        return binding.root
    }

    private fun playOnlineGame(rounds: Int) {
        val action = HomeFragmentDirections.actionNavHomeToMatchmakingFragment(rounds)
        findNavController().navigate(action)
    }

    /**
     * @param nEvents: number of Wins or Rounds depending on the game implementation
     */
    private fun playNRoundsWithComputer(nEvents: Int) {
        val randomPlayer = RandomPlayer(listOf(Hand.ROCK, Hand.PAPER, Hand.SCISSORS))
        matchViewModel.setGameServiceSettings(nEvents, randomPlayer)
        matchViewModel.startOfflineGameService()
        findNavController().navigate(R.id.gameFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setNavigationOnButton(buttonToBind: Button, fragmentID: Int) {
        buttonToBind.setOnClickListener { findNavController().navigate(fragmentID) }
    }
}
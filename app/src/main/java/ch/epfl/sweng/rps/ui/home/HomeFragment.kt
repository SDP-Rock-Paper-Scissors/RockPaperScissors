package ch.epfl.sweng.rps.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.databinding.FragmentHomeBinding
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.models.RandomPlayer

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            buttonPlay1GamesOffline.setOnClickListener { playNRoundsWithComputer(1) }
        }
    }

    /**
     * @param nEvents: number of Wins or Rounds depending on the game implementation
     */
    private fun playNRoundsWithComputer(nEvents: Int) {
        val randomPlayer = RandomPlayer(listOf(Hand.ROCK, Hand.PAPER, Hand.SCISSORS))
        matchViewModel.startOfflineGameService(nEvents, randomPlayer)
        findNavController().navigate(R.id.gameFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
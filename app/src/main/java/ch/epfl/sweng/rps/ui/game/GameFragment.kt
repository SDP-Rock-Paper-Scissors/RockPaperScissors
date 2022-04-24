package ch.epfl.sweng.rps.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.databinding.FragmentGameBinding
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.ui.home.MatchViewModel

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!
    private val matchViewModel: MatchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rockRB.setOnClickListener { rpsPressed(Hand.ROCK) }
        binding.paperRB.setOnClickListener { rpsPressed(Hand.PAPER) }
        binding.scissorsRB.setOnClickListener { rpsPressed(Hand.SCISSORS) }

    }

    private fun rpsPressed(hand: Hand) {
        matchViewModel.playHand(hand) {
            findNavController().navigate(R.id.action_gameFragment_to_gameResultFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
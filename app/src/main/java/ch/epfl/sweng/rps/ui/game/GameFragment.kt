package ch.epfl.sweng.rps.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
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
        matchViewModel.cumulativeScore.observe(
            viewLifecycleOwner
        ) {
            binding.opponentMatchData.currentPoints.text =
                matchViewModel.computerPlayerCurrentPoints
            binding.userMatchData.currentPoints.text = matchViewModel.userPlayerCurrentPoints
        }
    }

    private fun rpsPressed(hand: Hand) {
        // the button activates an asynchronous tasks
        // while the task is still running the clicking on the other radio buttons should NOT
        // start a new procedure and that's why the if statement in needed below
        if (matchViewModel.job == null ||
            (matchViewModel.job != null && !matchViewModel.job?.isActive!!)
        ) {
            matchViewModel.managePlayHand(hand,
                opponentsMoveUIUpdateCallback = {
                    opponentMoveUIUpdate(
                        matchViewModel.gameService?.currentRound?.hands?.get(
                            matchViewModel.computerPlayer!!.computerPlayerId
                        )!!
                    )
                },
                scoreBasedUpdatesCallback = {
                    matchViewModel.scoreBasedUpdates()
                },

                resultNavigationCallback = {
                    resultNavigation()
                },
                resetUIScoresCallback = {
                    resetScores()
                }
            )
        }
    }


    private fun resultNavigation() {
        if (matchViewModel.gameService?.isGameOver!!) {
            findNavController().navigate(R.id.action_gameFragment_to_gameResultFragment)
        } else {
            findNavController().navigate(R.id.action_gameFragment_self)
        }
    }

    private fun getOpponentRBBindingForHand(hand: Hand): RadioButton? {
        return when (hand) {
            Hand.ROCK -> binding.rockRBOpponent
            Hand.PAPER -> binding.paperRBOpponent
            Hand.SCISSORS -> binding.scissorsRBOpponent
            Hand.NONE -> null
        }
    }

    private fun opponentMoveUIUpdate(hand: Hand) {
        val radioButtonBinding = getOpponentRBBindingForHand(hand)
        radioButtonBinding?.isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun resetScores() {
        if (matchViewModel.gameService?.isGameOver!!) {
            binding.opponentMatchData.currentPoints.text = "0"
            binding.userMatchData.currentPoints.text = "0"
        }
    }
}
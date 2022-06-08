package ch.epfl.sweng.rps.ui.game

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.databinding.FragmentGameBinding
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.services.ServiceLocator
import ch.epfl.sweng.rps.ui.camera.CameraXLivePreviewActivityContract
import ch.epfl.sweng.rps.ui.home.MatchViewModel

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!
    private val matchViewModel: MatchViewModel by activityViewModels()
    private val activityLauncher =
        registerForActivityResult(CameraXLivePreviewActivityContract()) { result ->

            result ?: return@registerForActivityResult

            val r = Runnable {
                rpsPressed(result)
            }
            //delays call to rpsPressed by 1s. Otherwise the result would be
            //showed too quickly
            Handler(Looper.getMainLooper()).postDelayed(r, 1000)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rockIM.setOnClickListener { rpsPressed(Hand.ROCK) }
        binding.paperIM.setOnClickListener { rpsPressed(Hand.PAPER) }
        binding.scissorsIM.setOnClickListener { rpsPressed(Hand.SCISSORS) }
        binding.buttonActivateCamera.setOnClickListener { activityLauncher.launch(null) }
        setElementsToThemeColor()
        matchViewModel.cumulativeScore.observe(viewLifecycleOwner) {
            binding.opponentData.currentPoints.text =
                matchViewModel.computerPlayerCurrentPoints
            binding.hostData.currentPoints.text = matchViewModel.userPlayerCurrentPoints
        }
        matchViewModel.host.observe(viewLifecycleOwner) {
            binding.hostData.username.text = it?.username ?: "???"
        }
        matchViewModel.opponent.observe(viewLifecycleOwner) {
            binding.opponentData.username.text = it?.username ?: "???"
        }
    }

    override fun onStart() {
        super.onStart()
        val gameId = arguments?.getString("game_id")
        if (gameId != null) {
            Toast.makeText(context, "Game ID: $gameId", Toast.LENGTH_LONG).show()
            matchViewModel.setGameServiceSettingsOnline(
                requireActivity(),
                ServiceLocator.getInstance().getGameServiceForGame(gameId)
            )
            matchViewModel.gameService?.startListening()
        }
    }

    private fun rpsPressed(hand: Hand) {
        // the button activates an asynchronous tasks
        // while the task is still running the clicking on the other radio buttons should NOT
        // start a new procedure and that's why the if statement in needed below
        if (matchViewModel.job == null ||
            (matchViewModel.job != null && !matchViewModel.job?.isActive!!)
        ) {
            //inference of the button happens here due to the e.g. camera functionality
            val butttonBinding = getHostImageButtonBindingForHand(hand)

            animateChoice(butttonBinding)
            matchViewModel.managePlayHand(hand,
                opponentsMoveUIUpdateCallback = {
                    opponentMoveUIUpdate(
                        matchViewModel.gameService?.currentRound?.hands?.get(
                            matchViewModel.opponent.value!!.uid
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

    /**
     * Makes other than chosen hands dissapered.
     */
    private fun animateChoice(buttonBinding: ImageButton?) {
        var toDissaperButtons = listOf(binding.rockIM, binding.paperIM, binding.scissorsIM)
        toDissaperButtons = toDissaperButtons.filter { it != buttonBinding }
        for (button in toDissaperButtons) {
            button.isInvisible = true
        }
    }


    private fun resultNavigation() {
        if (matchViewModel.gameService?.isGameOver!!) {
            findNavController().navigate(R.id.action_gameFragment_to_gameResultFragment)
        } else {
            findNavController().navigate(R.id.action_gameFragment_self)
        }
    }

    private fun getHostImageButtonBindingForHand(hand: Hand): ImageButton? {
        return when (hand) {
            Hand.ROCK -> binding.rockIM
            Hand.PAPER -> binding.paperIM
            Hand.SCISSORS -> binding.scissorsIM
            Hand.NONE -> null
        }
    }

    private fun getOpponentRBBindingForHand(hand: Hand): ImageView? {
        return when (hand) {
            Hand.ROCK -> binding.rockOpponnent
            Hand.PAPER -> binding.paperOpponnent
            Hand.SCISSORS -> binding.scissorsOpponnent
            Hand.NONE -> null
        }
    }

    private fun opponentMoveUIUpdate(hand: Hand) {
        val opponentChoice = getOpponentRBBindingForHand(hand)
        binding.waitingForOpponent.isGone = true
        opponentChoice?.isVisible = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun resetScores() {
        if (matchViewModel.gameService?.isGameOver!!) {
            binding.opponentData.currentPoints.text = "0"
            binding.hostData.currentPoints.text = "0"
        }
    }


    /**
     * Applys theme color to ImageButton and SpinKit
     */
    private fun setElementsToThemeColor() {
        val typedValue = TypedValue()
        requireActivity().theme.resolveAttribute(
            R.attr.colorPrimary,
            typedValue,
            true
        )
        binding.buttonActivateCamera.drawable.setTint(typedValue.data)
        binding.waitingForOpponent.setColor(typedValue.data)
    }

}


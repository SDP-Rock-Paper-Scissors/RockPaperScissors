package ch.epfl.sweng.rps.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.databinding.FragmentGameBinding
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.ui.home.MatchViewModel

class GameFragment : Fragment(){

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!
    private val matchViewModel: MatchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rockRB.setOnClickListener{ rockPressed() }
        binding.paperRB.setOnClickListener{ paperPressed()}
        binding.scissorsRB.setOnClickListener{ scissorsPressed()}

    }

    private fun rockPressed() {
        val userResult = matchViewModel.determineRoundResults("toChangeroundId","toChangeUserUid", Hand.ROCK)
        moveToWinLoseDraw(userResult)
    }

    private fun paperPressed(){
        val userResult = matchViewModel.determineRoundResults("toChangeroundId","toChangeUserUid", Hand.PAPER)
        moveToWinLoseDraw(userResult)
    }

    private fun scissorsPressed(){
        val userResult = matchViewModel.determineRoundResults("toChangeroundId","toChangeUserUid", Hand.SCISSORS)
        moveToWinLoseDraw(userResult)
    }

    private fun moveToWinLoseDraw(result: Hand.Result) {
        //TODO: change findNavController
        when (result) {
            Hand.Result.WIN -> {
                findNavController().navigate(R.id.action_gameFragment_to_winFragment)
            }
            Hand.Result.LOSE -> {
                findNavController().navigate(R.id.action_gameFragment_to_loseFragment)
            }
            else -> {
                findNavController().navigate(R.id.action_gameFragment_to_drawFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
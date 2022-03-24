package ch.epfl.sweng.rps.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.databinding.FragmentGameBinding
import ch.epfl.sweng.rps.models.Hand

class GameFragment : Fragment(){

    private lateinit var binding: FragmentGameBinding
    private val viewModel: GameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rockRB.setOnClickListener{ rockPressed() }
        binding.paperRB.setOnClickListener{ paperPressed(view)}
        binding.scissorsRB.setOnClickListener{ scissorsPressed(view)}

    }

    private fun rockPressed() {
        val result = viewModel.determineWinner(Hand.ROCK)
        moveToWinLoseDraw(result)
    }

    private fun paperPressed(view: View){
        val result = viewModel.determineWinner(Hand.PAPER)
        moveToWinLoseDraw(result)
    }

    private fun scissorsPressed(view: View){
        val result = viewModel.determineWinner(Hand.SCISSORS)
        moveToWinLoseDraw(result)
    }

    private fun moveToWinLoseDraw(result: Hand.Result) {
        when (result) {
            Hand.Result.WIN -> {
                view?.findNavController()?.navigate(R.id.action_gameFragment_to_winFragment)
            }
            Hand.Result.LOSE -> {
                view?.findNavController()?.navigate(R.id.action_gameFragment_to_loseFragment)
            }
            else -> {
                view?.findNavController()?.navigate(R.id.action_gameFragment_to_drawFragment)
            }
        }
    }
}
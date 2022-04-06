package ch.epfl.sweng.rps.ui.game

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.databinding.FragmentGameResultBinding
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.ui.home.MatchViewModel

class GameResultFragment : Fragment() {

    private var _binding: FragmentGameResultBinding? = null
    private val binding get() = _binding!!
    private val matchViewModel: MatchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameResultBinding.inflate(inflater, container, false)

        binding.gameResultCommunicate.text = when (matchViewModel.currentUserResult){
            Hand.Result.WIN -> resources.getString(R.string.win)
            Hand.Result.LOSS -> resources.getString(R.string.lose)
            Hand.Result.TIE -> resources.getString(R.string.draw)
            else -> {throw IllegalStateException("Possible results are win, loose, tie.")}
        }
        return binding.root
    }
}
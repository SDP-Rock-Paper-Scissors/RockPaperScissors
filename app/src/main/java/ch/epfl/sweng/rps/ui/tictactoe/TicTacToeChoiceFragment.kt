package ch.epfl.sweng.rps.ui.tictactoe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import ch.epfl.sweng.rps.R


class TicTacToeChoiceFragment : Fragment() {

    private var choice = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tictactoe_choice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val noughtSelect = view.findViewById<ImageView>(R.id.ai_pick_side_circle_radio)
        val crossSelect = view.findViewById<ImageView>(R.id.ai_pick_side_cross_radio)
        val contBtn = view.findViewById<Button>(R.id.ai_pick_side_continue_btn)

        crossSelect.setOnClickListener {
            choice = 0
            crossSelect.setImageResource(R.drawable.radio_button_checked)
            noughtSelect.setImageResource(R.drawable.radio_button_unchecked)

        }

        noughtSelect.setOnClickListener {
            choice = 1
            noughtSelect.setImageResource(R.drawable.radio_button_checked)
            crossSelect.setImageResource(R.drawable.radio_button_unchecked)

        }

        contBtn.setOnClickListener {
//            val ticTacToeFragment = TicTacToeFragment()
//            val bundle = Bundle()
//            val transaction = requireActivity().supportFragmentManager.beginTransaction()
//            bundle.putInt("choice", choice)
//            ticTacToeFragment.arguments = bundle
//            transaction.replace(R.id.container, ticTacToeFragment)
//            transaction.commit()
            val action = TicTacToeChoiceFragmentDirections.startTicTacToe(choice)
            Navigation.findNavController(view).navigate(R.id.action_ticTacToeChoiceFragment_to_ticTacToeFragment2)
        }
    }

}
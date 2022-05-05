package ch.epfl.sweng.rps.ui.tictactoe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.services.OfflineTicTacToe
import ch.epfl.sweng.rps.services.TicTacToeGame

class TicTacToeFragment : Fragment() {


    private lateinit var viewModel: TicTacToeViewModel
    private var boxList = mutableListOf<ImageView>()
    val game = OfflineTicTacToe(this)
    lateinit var outcomeText: TextView
    lateinit var choice: TicTacToeGame.MOVES
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tictactoe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        outcomeText = view.findViewById(R.id.outcomeTTT)
        val argument: Int = requireArguments().getInt("choice")
        choice = if (argument == 0) TicTacToeGame.MOVES.CROSS else TicTacToeGame.MOVES.CIRCLE
        val boxMatrix = view.findViewById<LinearLayout>(R.id.matrix)
        val boxes = boxMatrix.children
            .filter { it is LinearLayout }
            .flatMap { v -> (v as LinearLayout).children }
            .filter { it is ImageView }
            .toList()
        boxes.forEach { boxList.add(it as ImageView) }
        boxes.forEachIndexed { i, b -> b.setOnClickListener { game.putChoice(choice, i) } }
    }

    fun updateUI(index: Int, choice: TicTacToeGame.MOVES) {
        val img = if (choice == TicTacToeGame.MOVES.CROSS) R.drawable.cross else R.drawable.nought
        boxList[index].setImageResource(img)
        boxList[index].tag = img
    }

    fun gameOver(winner: TicTacToeGame.MOVES) {
        outcomeText.visibility = View.VISIBLE
        if (winner == choice)
            outcomeText.text = "YOU WIN"
        else
            outcomeText.text = "YOU LOSE"
    }
}






package ch.epfl.sweng.rps.ui.tictactoe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.services.MultiplayerTicTacToe
import ch.epfl.sweng.rps.services.OfflineTicTacToe
import ch.epfl.sweng.rps.services.TicTacToeGame
import ch.epfl.sweng.rps.services.TicTacToeGame.MOVES

class TicTacToeFragment : Fragment() {


    private lateinit var viewModel: TicTacToeViewModel
    private var boxList = mutableListOf<ImageView>()
    lateinit var game: TicTacToeGame
    lateinit var outcomeText: TextView
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
        val mode: TicTacToeGame.MODE =
            (requireArguments().get("MODE") ?: TicTacToeGame.MODE.PC) as TicTacToeGame.MODE
        val choice: TicTacToeGame.MOVES =
            (requireArguments().get("PLAYER") ?: TicTacToeGame.MOVES.CROSS) as TicTacToeGame.MOVES
        if (mode == TicTacToeGame.MODE.PC)
            game = OfflineTicTacToe(this, choice)
        else
            game = MultiplayerTicTacToe(this, choice)
        val boxMatrix = view.findViewById<LinearLayout>(R.id.matrix)
        val boxes = boxMatrix.children
            .filter { it is LinearLayout }
            .flatMap { v -> (v as LinearLayout).children }
            .filter { it is ImageView }
            .toList()
        boxes.forEach { boxList.add(it as ImageView) }
        boxes.forEachIndexed { i, b -> b.setOnClickListener { game.putChoice(i) } }
    }

    fun updateUI(index: Int, choice: TicTacToeGame.MOVES) {
        val img = if (choice == TicTacToeGame.MOVES.CROSS) R.drawable.cross else R.drawable.nought
        boxList[index].setImageResource(img)
        boxList[index].tag = img
    }

    fun gameOver(winner: TicTacToeGame.MOVES) {
        outcomeText.visibility = View.VISIBLE
        when (winner) {
            MOVES.CROSS -> outcomeText.text = "CROSS WINS"
            MOVES.CROSS -> outcomeText.text = "CIRCLE WINS"
            MOVES.EMPTY -> outcomeText.text = "DRAW"
        }
    }
}






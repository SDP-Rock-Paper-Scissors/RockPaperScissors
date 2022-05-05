package ch.epfl.sweng.rps.ui.tictactoe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import ch.epfl.sweng.rps.R

class TicTacToeFragment : Fragment() {


    private lateinit var viewModel: TicTacToeViewModel
    private var boxList  = mutableListOf<ImageView>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        return inflater.inflate(R.layout.fragment_tictactoe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val choice = this.arguments?.getInt("choice")
        val box1 = view.findViewById<ImageView>(R.id.img_1)
        val box2 = view.findViewById<ImageView>(R.id.img_2)
        val box3 = view.findViewById<ImageView>(R.id.img_3)
        val box4 = view.findViewById<ImageView>(R.id.img_4)
        val box5 = view.findViewById<ImageView>(R.id.img_5)
        val box6 = view.findViewById<ImageView>(R.id.img_6)
        val box7 = view.findViewById<ImageView>(R.id.img_7)
        val box8 = view.findViewById<ImageView>(R.id.img_8)
        val box9 = view.findViewById<ImageView>(R.id.img_9)

        boxList.add(box1)
        boxList.add(box2)
        boxList.add(box3)
        boxList.add(box4)
        boxList.add(box5)
        boxList.add(box6)
        boxList.add(box7)
        boxList.add(box8)
        boxList.add(box9)

        box1.setOnClickListener {setChoice(0, choice!!) }
        box2.setOnClickListener {setChoice(1, choice!!) }
        box3.setOnClickListener {setChoice(2, choice!!) }
        box4.setOnClickListener {setChoice(3, choice!!) }
        box5.setOnClickListener {setChoice(4, choice!!) }
        box6.setOnClickListener {setChoice(5, choice!!) }
        box7.setOnClickListener {setChoice(6, choice!!) }
        box8.setOnClickListener {setChoice(7, choice!!) }
        box9.setOnClickListener {setChoice(8, choice!!) }


    }

    private fun setChoice(index: Int, choice: Int) {
        if(choice == 0){
            //0 represent cross "x"
            boxList[index].setImageResource(R.drawable.cross)
            boxList[index].tag = R.drawable.cross

        }else{
            boxList[index].setImageResource(R.drawable.nought)
            boxList[index].tag = R.drawable.nought
        }
    }


}



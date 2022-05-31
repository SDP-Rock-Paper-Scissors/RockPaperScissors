package ch.epfl.sweng.rps.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import ch.epfl.sweng.rps.R


class GameModeDialogFragment: DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_game_mode_dialog, container, false)

        rootView.findViewById<Button>(R.id.cancelButton).setOnClickListener{
            dismiss()
        }

        rootView.findViewById<Button>(R.id.confirmButton).setOnClickListener{
            val radioGroup: RadioGroup = rootView.findViewById(R.id.gameModeRadioGrp)
            val selectedID = radioGroup.checkedRadioButtonId
            val radio: RadioButton = rootView.findViewById(selectedID)

            if (radio.text == "Play five games"){
                dismiss()
                playOnlineGame(5)
            }
            else if (radio.text == "Play one game") {
                dismiss()
                playOnlineGame(1)
            }
            else {
                Toast.makeText(activity,"Please select a mode", Toast.LENGTH_SHORT).show()
            }
        }
        return rootView
    }
}

private fun playOnlineGame(rounds: Int) {
    findNavController(GameModeDialogFragment()).navigate(GameModeDialogFragmentDirections.actionGameModeDialogFragmentToMatchmakingFragment(rounds))
}


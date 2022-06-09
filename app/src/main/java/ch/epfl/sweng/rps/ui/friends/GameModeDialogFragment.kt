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
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.ui.game.MatchmakingFragment


class GameModeDialogFragment: DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_game_mode_dialog, container, false)

        rootView.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            dismiss()
        }
        rootView.findViewById<Button>(R.id.confirmButton).setOnClickListener {
            val radioGroup: RadioGroup = rootView.findViewById(R.id.gameModeRadioGrp)
            val selectedID = radioGroup.checkedRadioButtonId
            val matchFrg = MatchmakingFragment()
            val args = Bundle()

            if (selectedID != -1) {
                val radio: RadioButton = rootView.findViewById(selectedID)
                if (radio.text == "1 round") {

                    dismiss()
                    args.putInt("rounds", 1)
                    matchFrg.arguments = args
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, matchFrg).addToBackStack(null)
                        .commit()
                } else if (radio.text == "5 rounds") {
                    dismiss()
                    args.putInt("rounds", 5)
                    matchFrg.arguments = args
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, matchFrg).addToBackStack(null)
                        .commit()
                }
                } else {
                    Toast.makeText(activity, "Please select a mode", Toast.LENGTH_SHORT).show()
                }
            }
        return rootView
        }

    }


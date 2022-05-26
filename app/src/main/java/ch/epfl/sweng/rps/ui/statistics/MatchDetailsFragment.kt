package ch.epfl.sweng.rps.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.remote.FirebaseHelper
import kotlinx.coroutines.launch


class MatchDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        container?.removeAllViews()
        val matchUuid = this.arguments?.getString("uuid")
        val newView = inflater.inflate(R.layout.fragment_match_details, container, false)
        viewLifecycleOwner.lifecycleScope.launch {
            val matchDetailsList = FirebaseHelper.getMatchDetailData(matchUuid!!)
            Log.i("matchDetailsList", matchDetailsList.toString())
            for ((index, _, userHand, opponentHand, outcome) in matchDetailsList) {
                addDetailRow(
                    newView,
                    index.toString(),
                    userHand.asHandEmoji(),
                    opponentHand.asHandEmoji(),
                    outcome.asEmoji()
                )
            }
        }



        return newView
    }

    private fun addDetailRow(
        view: View,
        roundIndex: String,
        userChoice: String,
        opponentChoice: String,
        outcome: String,
    ) {
        val sizeInDp = 8
        val matchDetailTable = view.findViewById<TableLayout>(R.id.matchDetailTable)
        val row = TableRow(activity)
        row.setBackgroundColor(Color.parseColor("#0FF0F7F7"))
        val scale = resources.displayMetrics.density
        val dpAsPixels = (sizeInDp * scale + 0.5f)
        row.setPadding(dpAsPixels.toInt())


        val params = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT,
            1f
        )

        val roundIndexBlank = TextView(activity)
        val userChoiceBlank = TextView(activity)
        val opponentChoiceBlank = TextView(activity)
        val outcomeBlank = TextView(activity)

        roundIndexBlank.text = roundIndex
        userChoiceBlank.text = userChoice
        opponentChoiceBlank.text = opponentChoice
        outcomeBlank.text = outcome

        roundIndexBlank.layoutParams = params
        userChoiceBlank.layoutParams = params
        opponentChoiceBlank.layoutParams = params
        outcomeBlank.layoutParams = params

        row.addView(roundIndexBlank)
        row.addView(userChoiceBlank)
        row.addView(opponentChoiceBlank)
        row.addView(outcomeBlank)
        matchDetailTable?.addView(row)
    }
}
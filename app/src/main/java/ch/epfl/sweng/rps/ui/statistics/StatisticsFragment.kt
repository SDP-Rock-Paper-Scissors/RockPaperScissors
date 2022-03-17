package ch.epfl.sweng.rps.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.databinding.FragmentStatisticsBinding


class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val values = arrayOf(
            "Mode Filter",
            "3 Matches",
            "5 Matches",
            "12 Matches",
        )
        val newView = inflater.inflate(R.layout.fragment_statistics, container, false)
        val modeSpinner = newView.findViewById(R.id.modeSelect) as Spinner
        val adapter = ArrayAdapter(this.requireActivity(), android.R.layout.simple_spinner_item, values)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        modeSpinner.adapter = adapter
        modeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    1 -> println("x == 1")
                    2 -> println("x == 2")
                    else -> { // Note the block
                        println("x is neither 1 nor 2")
                    }
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }
        return newView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addPersonalStats(view,"b9d5384-9f1f-11ec-b909-0242ac120002","2022-03-09","test","12", "4:8")

    }




    private fun addPersonalStats(view: View, uuid: String, date:String, opponent:String, mode:String, score: String ) {
        val sizeInDp = 5
        val statsTable = view.findViewById<TableLayout>(R.id.statsTable)
        val row = TableRow(activity)
        row.setBackgroundColor(
            Color.parseColor("#F0F7F7"))
        val scale = resources.displayMetrics.density
        val dpAsPixels = (sizeInDp * scale + 0.5f)
        row.setPadding(dpAsPixels.toInt())
        row.isClickable
        row.tag = uuid

        row.setOnClickListener {
            // add new fragment with communication
            val matchDetailFragment = MatchDetails()
            val bundle = Bundle()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            bundle.putString("uuid",uuid)
            matchDetailFragment.arguments =  bundle
            transaction.replace(R.id.fragment_statistics,matchDetailFragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }

        val params = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT,
            1f
        )

        val dateBlank = TextView(activity)
        val opponentBlank = TextView(activity)
        val modeBlank = TextView(activity)
        val scoreBlank = TextView(activity)

        dateBlank.text = date
        opponentBlank.text = opponent
        modeBlank.text = mode
        scoreBlank.text = score

        dateBlank.layoutParams = params
        opponentBlank.layoutParams = params
        modeBlank.layoutParams = params
        scoreBlank.layoutParams = params

        row.addView(dateBlank)
        row.addView(opponentBlank)
        row.addView(modeBlank)
        row.addView(scoreBlank)
        statsTable.addView(row)

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
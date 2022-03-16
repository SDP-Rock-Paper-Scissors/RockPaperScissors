package ch.epfl.sweng.rps.ui.statistics

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
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

        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addPersonalStats(view,"0b9d5384-9f1f-11ec-b909-0242ac120002","2022-03-09","test","12", "4:8")

    }




    private fun addPersonalStats(view: View, uuid: String, date:String, opponent:String, mode:String, score: String ) {
        val sizeInDp = 5
        val statsTable = view.findViewById<TableLayout>(R.id.statsTable)
        val row = TableRow(activity)
        row.setBackgroundColor(
            Color.parseColor("#F0F7F7"))
        val scale = resources.displayMetrics.density
        val dpAsPixels = (sizeInDp * scale + 0.5f)
        row.isClickable
        row.setPadding(dpAsPixels.toInt())
        row.tag = uuid
        row.setOnClickListener {
            // add new fragment

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment_content_main,MatchDetails())
            transaction.commit()
            // add new activity
            /*
            val intent =  Intent()
            intent.setClass(requireActivity(), MatchDetail::class.java)
            intent.putExtra("matchUuid", row.tag as String)
            startActivity(intent)
            */


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
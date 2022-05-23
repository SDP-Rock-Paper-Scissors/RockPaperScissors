package ch.epfl.sweng.rps.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.databinding.FragmentStatisticsBinding
import ch.epfl.sweng.rps.models.ui.UserStat
import ch.epfl.sweng.rps.persistence.Cache


class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private lateinit var cache: Cache

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val newView = inflater.inflate(R.layout.fragment_statistics, container, false)
        val modeSpinner = newView.findViewById(R.id.modeSelect) as Spinner
        val modes = getGameModes()
        val adapter =
            ArrayAdapter(this.requireActivity(), android.R.layout.simple_spinner_item, modes)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        modeSpinner.adapter = adapter
        return newView
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        cache = Cache.getInstance()!!
        val modeSpinner = itemView.findViewById(R.id.modeSelect) as Spinner
        val statsRecyclerView = itemView.findViewById<RecyclerView>(R.id.stats_recycler_view)
        val model: StatisticsViewModel by viewModels()
        val fragmentManager = requireActivity().supportFragmentManager

        modeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                statsRecyclerView.removeAllViews()
                statsRecyclerView.apply {
                    layoutManager = LinearLayoutManager(activity)
                    adapter = StatsItemAdapter(fragmentManager)
                    setHasFixedSize(true)
                }
                model.getStats(position).observe(viewLifecycleOwner, Observer { stats ->
                    showStats(
                        itemView, stats
                    )

                })

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }


    }


    private fun showStats(itemView: View, stat: List<UserStat>) {
        val adapter =
            itemView.findViewById<RecyclerView>(R.id.stats_recycler_view).adapter as StatsItemAdapter
        adapter.addStats(stat)

    }


    private fun getGameModes(): Array<String> {

        return arrayOf(
            "Mode Filter",
            "Rock-Paper-Scissor",
            "Tic-Tac-Toe",
        )

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
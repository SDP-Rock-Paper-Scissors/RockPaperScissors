package ch.epfl.sweng.rps.ui.statistics


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.databinding.FragmentStatisticsBinding
import ch.epfl.sweng.rps.db.FirebaseHelper
import kotlinx.coroutines.launch


class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // for filter
        val values = arrayOf(
            "Mode Filter",
            "Best of 3",
            "Best of 5",
            "Best of 12",
        )
        val newView = inflater.inflate(R.layout.fragment_statistics, container, false)
        val modeSpinner = newView.findViewById(R.id.modeSelect) as Spinner
        val adapter =
            ArrayAdapter(this.requireActivity(), android.R.layout.simple_spinner_item, values)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        modeSpinner.adapter = adapter
        modeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                // delete all rows except the title
                val statsTableLayout = view?.findViewById<TableLayout>(R.id.statsTable)
                while( statsTableLayout?.childCount!! > 1){
                    statsTableLayout.removeView(statsTableLayout.getChildAt(statsTableLayout.childCount -1 ))

                }

                println(position)
                //filter function
                viewLifecycleOwner.lifecycleScope.launch {
                    val statsDataList = FirebaseHelper.getStatsData(position)
                    for(statsData in statsDataList){
                        addPersonalStats(view!!, statsData[0],statsData[1],statsData[2],statsData[3],statsData[4])
                    }
                    //test by default
                    if(position == 0){
                        addPersonalStats(view!!,"000000","2022-04-05","Jinglun Pan","3","2 - 1")
                    }

                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }
        return newView
    }


    private fun addPersonalStats(
        view: View,
        uuid: String,
        date: String,
        opponent: String,
        mode: String,
        score: String
    ) {
        val sizeInDp = 5
        val statsTable = view.findViewById<TableLayout>(R.id.statsTable)
        val row = TableRow(activity)
        row.setBackgroundColor(
            Color.parseColor("#F0F7F7")
        )
        val scale = resources.displayMetrics.density
        val dpAsPixels = (sizeInDp * scale + 0.5f)

        row.id = R.id.test_for_stats_row
        row.setPadding(dpAsPixels.toInt())
        row.isClickable
        row.tag = uuid
        //only for test


        row.setOnClickListener {
            // add new fragment with communication
            val matchDetailFragment = MatchDetails()
            val bundle = Bundle()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            bundle.putString("uuid", uuid)
            matchDetailFragment.arguments = bundle
            transaction.replace(R.id.fragment_statistics, matchDetailFragment)
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
        statsTable?.addView(row)

    }




    /*
    private fun readStatsData(userID: String, firebaseCallBack: FirebaseCallBack) {
        val usersDB = Firebase.firestore.collection("users")
        val matchesDB = Firebase.firestore.collection("matches")
        val allStatsResult: MutableList<List<String>> = ArrayList()
        val statsResult: MutableList<String> = ArrayList()
        //return list format: match_uuid, date,opponent, mode, score
        usersDB.document(userID).get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    val matchHistory: List<String> = document.data?.get("matchesList") as List<String>
                    for (matchUUID in matchHistory) {
                        //retrieve match_uuid for next detail page query
                        statsResult.add(matchUUID)
                        matchesDB.document(matchUUID).get()
                            .addOnSuccessListener { doc ->
                                if (doc != null) {
                                    //retrieve first component: date
                                    val matchDate: Timestamp =
                                        doc.data?.get("match_date") as Timestamp
                                    val matchDateFormat =
                                        SimpleDateFormat("yyyy-MM-dd").format(matchDate.toDate())
                                    statsResult.add(matchDateFormat)
                                    //retrieve second component: opponent
                                    val details =
                                        doc.data?.get("details") as HashMap<String, String>
                                    val players = details.keys
                                    for (playerID in players) {
                                        if (playerID != userID) {
                                            statsResult.add(playerID)
                                        }
                                    }
                                    //retrieve third component: mode
                                    val mode = doc.data?.get("mode")
                                    statsResult.add(mode as String)
                                    //retrieve fourth component: score
                                    val overallScore =
                                        doc.data?.get("overall_score") as HashMap<String, String>
                                    val playerScore = overallScore[userID]
                                    val opponentScore = overallScore[statsResult[2]]
                                    val score = "$playerScore - $opponentScore"
                                    statsResult.add(score)
                                    usersDB.document(statsResult[2]).get()
                                        .addOnSuccessListener { result ->
                                            val opponentName = result.data?.get("username")
                                            statsResult[2] = opponentName as String
                                            allStatsResult.add(statsResult)
                                            firebaseCallBack.onCallBack(allStatsResult)
                                        }


                                } else {
                                    Log.d(TAG, "No such document in matchDB")
                                }
                            }
                    }
                } else {
                    Log.d(TAG, "No such document in userDB.")
                }

            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }



    }

     */


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}
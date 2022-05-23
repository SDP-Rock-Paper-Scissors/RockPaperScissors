package ch.epfl.sweng.rps.ui.friends

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.models.ui.FakeFriendsData
import ch.epfl.sweng.rps.models.ui.FriendsInfo


class FriendsFragment : Fragment(), FriendListAdapter.OnButtonClickListener {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val friends = FakeFriendsData.myFriendsData
        val recyclerView = view.findViewById<RecyclerView>(R.id.friendListRecyclerView)


        //Get info from Fake Data object and display
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = FriendListAdapter(friends, this)

    }

    //Button Click Listeners
    override fun onButtonClick(position: Int, friends: List<FriendsInfo>, view: View) {
        val username = friends[position].username
        val gamesPlayed = friends[position].gamesPlayed
        val gamesWon = friends[position].gamesWon
        val winRate = friends[position].winRate
        val isOnline = friends[position].isOnline


        //if info button is clicked
        if (view == view.findViewById(R.id.infoButton)) {
            Log.i("Press info", "This is $username's info")
            Toast.makeText(activity, "This is $username's info", Toast.LENGTH_SHORT).show()
            //Move to infoPage on button click
            findNavController().navigate(
                FriendsFragmentDirections.actionNavFriendsToInfoPageFragment3(
                    //Passing all the info to be displayed in the Info Page
                    username,
                    "Games Played: $gamesPlayed",
                    "Games Won: $gamesWon",
                    "Win Rate: $winRate%",
                    isOnline
                )
            )


        }
        //if play button is clicked
        else if (view == view.findViewById(R.id.playButton)) {
            Log.i("Press info", "You will play a game with $username")
            Toast.makeText(activity, "You will play a game with $username", Toast.LENGTH_SHORT)
                .show()

            //Move to game fragment on button click
            findNavController().navigate(
                FriendsFragmentDirections.actionNavFriendsToGameFragment2(
                    null
                )
            )
        }
    }


}
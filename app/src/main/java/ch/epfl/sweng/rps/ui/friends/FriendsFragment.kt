package ch.epfl.sweng.rps.ui.friends

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.epfl.sweng.rps.FriendListAdapter
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.models.FakeFriendsData
import ch.epfl.sweng.rps.models.FriendsInfo
import java.util.*
import kotlin.collections.ArrayList
import android.widget.SearchView
import androidx.navigation.NavDirections
import kotlinx.android.synthetic.main.fragment_friends.*


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
        val searchView = view.findViewById<SearchView>(R.id.userNameSearch)
        val filterFriend = mutableListOf<FriendsInfo>()
        val requestBtn = view.findViewById<ImageButton>(R.id.requestButton)

        //Get info from Fake Data object and display
        filterFriend.addAll(friends)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = FriendListAdapter(filterFriend, this)

        requestBtn.setOnClickListener{
            findNavController().navigate(FriendsFragmentDirections.actionNavFriendsToRequestFragment())
        }

       searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }



            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String?): Boolean {
                filterFriend.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()){

                    friends.forEach{

                        if (it.username.toLowerCase(Locale.getDefault()).contains(searchText)){
                            filterFriend.add(it)
                        }
                    }
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
                else {
                    filterFriend.clear()
                    filterFriend.addAll(friends)
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
                return false
            }
        })

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
            findNavController().navigate(FriendsFragmentDirections.actionNavFriendsToInfoPageFragment3(
                //Passing all the info to be displayed in the Info Page
                username,
                "Games Played: $gamesPlayed",
                "Games Won: $gamesWon",
                "Win Rate: $winRate%",
                isOnline))


        }
        //if play button is clicked
        else if (view == view.findViewById(R.id.playButton)){
            Log.i("Press info", "You will play a game with $username")
            Toast.makeText(activity, "You will play a game with $username", Toast.LENGTH_SHORT).show()

            //Move to game fragment on button click
            findNavController().navigate(FriendsFragmentDirections.actionNavFriendsToGameFragment2())
        }

    }



}
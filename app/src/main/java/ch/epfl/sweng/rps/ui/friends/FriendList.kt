package ch.epfl.sweng.rps.ui.friends

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.epfl.sweng.rps.FriendListAdapter
import ch.epfl.sweng.rps.FriendsInfo
import ch.epfl.sweng.rps.R

class FriendList : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val friends = listOf(
            FriendsInfo("RPSKing88", 120, 76, 63.3, true),
            FriendsInfo("Meliodas19", 220, 110, 50.0, true),
            FriendsInfo("Urtrash", 86, 32, 37.2, true),
            FriendsInfo("Ben10", 14, 12, 85.7, true),
            FriendsInfo("JustGary", 455, 343, 75.4, false),
            FriendsInfo("RockFirst", 141, 118, 83.7, false),
            FriendsInfo("ulose", 63, 28, 44.4, false),
            FriendsInfo("GameMstr", 90, 12, 13.3, false),
            FriendsInfo("Narut0", 312, 211, 67.6, false),
            FriendsInfo("Insomnix", 166, 65, 39.2, false)
        )
        val recyclerView = view.findViewById<RecyclerView>(R.id.friendListRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = FriendListAdapter(friends)

    }

}
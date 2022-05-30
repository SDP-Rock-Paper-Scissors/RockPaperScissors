package ch.epfl.sweng.rps.ui.friends

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.RequestListAdapter
import ch.epfl.sweng.rps.models.FriendRequest
import ch.epfl.sweng.rps.models.FriendRequestInfo
import ch.epfl.sweng.rps.persistence.Cache
import ch.epfl.sweng.rps.services.ServiceLocator
import kotlinx.coroutines.launch

class MyFriendRequestsFragment : Fragment(), RequestListAdapter.OnButtonClickListener {
    private lateinit var cache: Cache

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_friend_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.myReqsRecyclerView)
        val model:MyFriendsRequestsModel by viewModels()
        recyclerView.layoutManager = LinearLayoutManager(activity)

        /* val reqs = mutableListOf<FriendRequestInfo>()

        viewLifecycleOwner.lifecycleScope.launch{
            val f = FirebaseHelper.getFriendReqs()
            Log.i("ReqFragment", "reqs:${f}")
            reqs.addAll(f)
            recyclerView.adapter!!.notifyDataSetChanged()
        } 
        recyclerView.adapter = RequestListAdapter(reqs ,this) */

        // Get requests from cache
        cache = Cache.getInstance()!!
        model.getFriendReqs().observe(viewLifecycleOwner) { reqs ->
            recyclerView.adapter = RequestListAdapter(reqs, this)
        }
    }

    override fun onButtonClick(position: Int, reqs: List<FriendRequestInfo>, view: View) {
        val username = reqs[position].username
        val uid = reqs[position].uid


        //if info button is clicked
        if (view == view.findViewById(R.id.acceptButton)) {
            Log.i("Accept req", "You have accepted $username's request")
            Toast.makeText(activity, "You have accepted $username's request", Toast.LENGTH_SHORT).show()

            viewLifecycleOwner.lifecycleScope.launch{
                ServiceLocator.getInstance().repository.changeFriendRequestToStatus(uid, FriendRequest.Status.ACCEPTED)
            }
        }
        //if play button is clicked
        else if (view == view.findViewById(R.id.rejectButton)){
            Log.i("Reject req", "You have rejected $username's request")
            Toast.makeText(activity, "You have rejected $username's request", Toast.LENGTH_SHORT).show()

            viewLifecycleOwner.lifecycleScope.launch{
                ServiceLocator.getInstance().repository.changeFriendRequestToStatus(uid, FriendRequest.Status.REJECTED)
            }
        }

    }
}
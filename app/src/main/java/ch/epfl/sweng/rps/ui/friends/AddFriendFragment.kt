package ch.epfl.sweng.rps.ui.friends

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.db.FirebaseHelper
import ch.epfl.sweng.rps.db.FirebaseRepository
import ch.epfl.sweng.rps.services.ServiceLocator
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch


class AddFriendFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_friend, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sendReqBtn = view.findViewById<Button>(R.id.sendRequestButton)


        sendReqBtn.setOnClickListener{

            val editText = view.findViewById<EditText>(R.id.addUserName)
            val uid = editText.text.toString()

            viewLifecycleOwner.lifecycleScope.launch{
                val myUid = ServiceLocator.getInstance().repository.rawCurrentUid().toString()

                when {
                    uid == myUid -> {
                        Toast.makeText(activity, "Do not enter your own user ID", Toast.LENGTH_SHORT).show()
                        Log.i("IDs", "mine:$myUid, sent to:$uid")
                    }
                    uid == "" -> {
                        Toast.makeText(activity, "Please enter a user ID", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        ServiceLocator.getInstance().repository.sendFriendRequestTo(uid)
                        Toast.makeText(activity, "You have sent a request", Toast.LENGTH_SHORT).show()
                        Log.i("IDs", "mine:$myUid, sent to:$uid")
                    }
                }
            }
        }
    }
}


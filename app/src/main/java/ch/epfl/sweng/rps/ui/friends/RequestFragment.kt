package ch.epfl.sweng.rps.ui.friends

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import ch.epfl.sweng.rps.R
import kotlinx.android.synthetic.main.fragment_match_details.*
import kotlinx.android.synthetic.main.request_fragment.*

class RequestFragment : Fragment() {

    companion object {
        fun newInstance() = RequestFragment()
    }

    private lateinit var viewModel: RequestViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.request_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addFrdFrg = AddFriendFragment()
        val myFrdReqFrg = MyFriendRequestsFragment()
        val addFrdBtn = view.findViewById<Button>(R.id.addFriendsButton)
        val myFrdReqBtn = view.findViewById<Button>(R.id.myFriendReqButton)

        childFragmentManager.beginTransaction().apply {
            add(R.id.fragmentContainerView, addFrdFrg).commit()
        }
            addFrdBtn.setOnClickListener{
                childFragmentManager.beginTransaction().apply {
                    replace(R.id.fragmentContainerView, addFrdFrg).commit()
                }
            }

            myFrdReqBtn.setOnClickListener{
                childFragmentManager.beginTransaction().apply {
                    replace(R.id.fragmentContainerView, myFrdReqFrg).commit()
                }
            }
        }

    }

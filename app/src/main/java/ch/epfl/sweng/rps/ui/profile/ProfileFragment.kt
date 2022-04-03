package ch.epfl.sweng.rps.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ch.epfl.sweng.rps.MainActivity
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.models.User
import ch.epfl.sweng.rps.ui.SettingsFragment


class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var user: User
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        user = (activity as MainActivity).getUserDetails()
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.TextEmail).text = user.email
        view.findViewById<TextView>(R.id.TextDisplayName).text = user.username
        view.findViewById<TextView>(R.id.TextPrivacy).text = user.gamesHistoryPrivacy
        view.findViewById<Button>(R.id.elevatedButton).setOnClickListener {
            val nextFrag = SettingsFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.profile_fragment, nextFrag, "settings")
                .addToBackStack(null)
                .commit()
        }
    }

}
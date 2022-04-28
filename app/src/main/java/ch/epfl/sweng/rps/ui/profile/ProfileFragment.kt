package ch.epfl.sweng.rps.ui.profile

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ch.epfl.sweng.rps.MainActivity
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.models.User
import ch.epfl.sweng.rps.ui.settings.SettingsActivity
import com.google.android.material.appbar.MaterialToolbar


class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var user: User

    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
        println(res)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        val view =  inflater.inflate(R.layout.profile_fragment, container, false)
        val button:Button = view.findViewById(R.id.editProfilePic)
        button.setOnClickListener{ getPicture()}
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = (activity as MainActivity).getUserDetails()
        view.findViewById<TextView>(R.id.TextEmail).text = user.email
        view.findViewById<TextView>(R.id.TextDisplayName).text = user.username
        view.findViewById<TextView>(R.id.TextPrivacy).text = user.games_history_privacy

        view.findViewById<MaterialToolbar>(R.id.profile_top_toolbar)
            .setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.profile_appbar_settings_btn -> {
                        val intent = Intent(activity, SettingsActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
    }
    private fun getPicture(){
        val intent = Intent()
        intent.type = "image/*";
        intent.action = Intent.ACTION_PICK
        intent.setDataAndType ( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*" )
        resultLauncher.launch(intent)
    }
    companion object{
        val PICK_IMAGE = 1
    }
}
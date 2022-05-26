package ch.epfl.sweng.rps

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import ch.epfl.sweng.rps.databinding.ActivityMainBinding
import ch.epfl.sweng.rps.models.User
import ch.epfl.sweng.rps.persistence.Cache
import ch.epfl.sweng.rps.ui.settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var currentUser: User
    private lateinit var cache:Cache

    override fun onCreate(savedInstanceState: Bundle?) {
        SettingsActivity.applyTheme(getString(R.string.theme_pref_key), sharedPreferences)
        super.onCreate(savedInstanceState)
        cache = Cache.getInstance() ?: Cache.createInstance(this)

        if(intent.action.equals("fromCamera")){
            val extras = intent.extras
            if (extras != null) {
                val value = extras.getString("result")
                Toast.makeText(this, "The pose $value was detected", Toast.LENGTH_SHORT).show()
            }
        } else {
            val userData: Bundle? = intent.extras?.getBundle("User")
            if (userData != null) {
                currentUser = User(
                    userData.getString("display_name"),
                    userData.getString("uid")!!,
                    userData.getString("privacy")!!,
                    false,
                    userData.getString("email")
                )
            }
        }


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)

    }


    fun getUserDetails(): User {
        return currentUser
    }

    private fun setupNav() {
          val navController = findNavController(R.id.nav_host_fragment_activity_main)
          val navView: BottomNavigationView = binding.navView
          navView.setupWithNavController(navController)

          //removes botttomNavView for specified fragments.
          navController.addOnDestinationChangedListener { _, destination, _ ->
              when (destination.id) {
                  R.id.gameFragment -> setBottomNavigationVisibility(View.GONE)
                  else -> setBottomNavigationVisibility(View.VISIBLE)
              }
          }
      }

    private fun setBottomNavigationVisibility(visibility: Int) {
        // get the reference of the bottomNavigationView and set the visibility.
        binding.navView.visibility = visibility
    }
}

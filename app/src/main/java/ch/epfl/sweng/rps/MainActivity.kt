package ch.epfl.sweng.rps

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import ch.epfl.sweng.rps.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import ch.epfl.sweng.rps.models.User
import ch.epfl.sweng.rps.ui.settings.SettingsActivity
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)

        SettingsActivity.applyTheme(getString(R.string.theme_pref_key), sharedPreferences)
    }


    fun getUserDetails(): User {
        return currentUser
    }

    /*  private fun setupNav() {
          val navController = findNavController(R.id.nav_host_fragment_activity_main)
          val navView: BottomNavigationView = binding.navView
          navView.setupWithNavController(navController)

          //removes botttomNavView for specified fragments.
          navController.addOnDestinationChangedListener { _, destination, _ ->
              when (destination.id) {
                  R.id.cameraFragment -> setBottomNavigationVisibility(View.GONE)
                  R.id.gameFragment -> setBottomNavigationVisibility(View.GONE)
                  else -> setBottomNavigationVisibility(View.VISIBLE)
              }
          }
      }*/

    private fun setBottomNavigationVisibility(visibility: Int) {
        // get the reference of the bottomNavigationView and set the visibility.
        binding.navView.visibility = visibility
    }
}

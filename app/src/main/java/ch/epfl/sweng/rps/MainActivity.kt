package ch.epfl.sweng.rps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import ch.epfl.sweng.rps.databinding.ActivityMainBinding
import ch.epfl.sweng.rps.ui.settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        SettingsActivity.applyTheme(getString(R.string.theme_pref_key), sharedPreferences)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)

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

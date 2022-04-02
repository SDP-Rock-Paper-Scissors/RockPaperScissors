package ch.epfl.sweng.rps

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import ch.epfl.sweng.rps.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import ch.epfl.sweng.rps.models.User
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var currentUser: User
        override fun onCreate(savedInstanceState: Bundle?) {
            var userData: Bundle? = intent.extras?.getBundle("User")
            if (userData != null) {
                currentUser = User(
                    userData.getString("display_name"),
                    userData.getString("uid")!!,
                    userData.getString("privacy")!!,
                    false,
                    userData.getString("email")
                )
            }
            super.onCreate(savedInstanceState)

            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val navView: BottomNavigationView = binding.navView

            val navController = findNavController(R.id.nav_host_fragment_activity_main)




            navView.setupWithNavController(navController)
        }

    fun getUserDetails() : User{
        return currentUser
    }
}

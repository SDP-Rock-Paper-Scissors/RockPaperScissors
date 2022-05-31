package ch.epfl.sweng.rps

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import ch.epfl.sweng.rps.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var coordinatorLayout: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        coordinatorLayout = binding.coordinatorLayout
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()

        if (intent.action.equals("fromCamera")) {
            val extras = intent.extras
            if (extras != null) {
                val value = extras.getString("result")
                Toast.makeText(this, "The pose $value was detected", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

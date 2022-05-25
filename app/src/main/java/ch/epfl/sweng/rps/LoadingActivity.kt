package ch.epfl.sweng.rps

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import ch.epfl.sweng.rps.persistence.Cache
import ch.epfl.sweng.rps.ui.onboarding.OnBoardingActivity
import ch.epfl.sweng.rps.utils.FirebaseEmulatorsUtils
import ch.epfl.sweng.rps.utils.L
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import kotlinx.coroutines.delay

class LoadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
    }


    init {
        lifecycleScope.launchWhenStarted { setupApp() }
    }

    val startOnboarding =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == OnBoardingActivity.RESULT_ONBOARDING_FINISHED) {
                log.log("Onboarding finished")
                hasAlreadyOnboarded = true
                lifecycleScope.launchWhenStarted {
                    nav()
                }
            }
        }


    /**
     * Here logic to setup the app
     */
    private suspend fun logic() {
        log.log("logic")

        Firebase.initialize(this@LoadingActivity)
        Cache.initialize(this@LoadingActivity)
        useEmulatorsIfNeeded()
        delay(1000)
    }

    private fun openLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun useEmulatorsIfNeeded() {
        val use = intent.getStringExtra("USE_EMULATORS")
        log.log("USE_EMULATORS: $use")
        if (use == "true") {
            if (isTest) {
                throw IllegalStateException("Emulators should not be used in tests")
            }
            FirebaseEmulatorsUtils.useEmulators()
            log.w("Using emulators")
        }
    }

    val isTest: Boolean
        get() = intent.getBooleanExtra("isTest", false)

    private suspend fun setupApp() {
        // All the logic here is to check if the user is logged in or not
        logic()

        if (!isTest) {
            nav()
        }
    }


    val log = L.of(this)


    private suspend fun nav() {
        log.log("nav")

        if (!hasAlreadyOnboarded && OnBoardingActivity.isFirstTime(this)) {
            log.log("nav to onboarding")
            startOnboarding.launch(OnBoardingActivity.createIntent(this))
            return
        }
        val user = Cache.getInstance().getUserDetails()
        log.log("user: $user")
        if (user == null) {
            log.log("nav to login")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        log.log("nav to main")
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private var hasAlreadyOnboarded = false


    companion object {
        const val IS_TEST_EXTRA = "isTest"
    }
}
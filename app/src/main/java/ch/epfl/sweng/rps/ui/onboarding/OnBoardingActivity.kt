package ch.epfl.sweng.rps.ui.onboarding

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceManager
import ch.epfl.sweng.rps.R
import com.ramotion.paperonboarding.PaperOnboardingFragment
import com.ramotion.paperonboarding.PaperOnboardingPage


class OnBoardingActivity : AppCompatActivity() {

    companion object {
        fun isFirstTime(context: Context): Boolean {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPref.getBoolean(
                context.getString(R.string.is_first_time), true
            )
        }

        fun setFirstTime(context: Context, isFirstTime: Boolean) {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPref.edit().putBoolean(
                context.getString(R.string.is_first_time), isFirstTime
            ).apply()
        }

        fun createIntent(
            context: Activity,
            onDone: AfterOnboardingAction = AfterOnboardingAction.FINISH
        ): Intent {
            val intent = Intent(context, OnBoardingActivity::class.java)
            intent.putExtra(AFTER_ONBOARDING_DONE_EXTRA, onDone)
            return intent
        }

        const val RESULT_ONBOARDING_FINISHED = 0x42069
        const val AFTER_ONBOARDING_DONE_EXTRA = "after_onboarding_done"
    }

    enum class AfterOnboardingAction {
        FINISH,
    }

    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.on_boarding_activity)
        fragmentManager = supportFragmentManager

        // new instance is created and data is took from an
        // array list known as getDataonborading
        val paperOnboardingFragment = PaperOnboardingFragment.newInstance(getDataforOnboarding())
        paperOnboardingFragment.setOnRightOutListener { navOut() }
        val fragmentTransaction = fragmentManager.beginTransaction()

        // fragmentTransaction method is used
        // do all the transactions or changes
        // between different fragments
        fragmentTransaction.add(R.id.onboarding_layout, paperOnboardingFragment)

        // all the changes are committed
        fragmentTransaction.commit()
    }


    private fun getDataforOnboarding(): ArrayList<PaperOnboardingPage> {

        // the first string is to show the main title ,
        // second is to show the message below the
        // title, then color of background is passed ,
        // then the image to show on the screen is passed
        // and at last icon to navigate from one screen to other
        val source = PaperOnboardingPage(
            "RPS",
            "Welcome to Rock Paper Scissors !",
            Color.parseColor("#efdeff"),
            R.mipmap.ic_launcher_round,
            R.drawable.ic_baseline_navigate_next_24
        )
        val source1 = PaperOnboardingPage(
            "Play",
            "Play with your friends and see who is the best !",
            Color.parseColor("#ace7ff"),
            R.drawable.ic_baseline_videogame_asset_24,
            R.drawable.ic_baseline_navigate_next_24
        )
        val source2 = PaperOnboardingPage(
            "Statistics",
            "Check your statistics, define your strategy, and try to beat your friends !",
            Color.parseColor("#f3ffe3"),
            R.drawable.ic_baseline_leaderboard_24,
            R.drawable.ic_baseline_navigate_next_24
        )

        return listOf(
            source,
            source1,
            source2
        ).toCollection(ArrayList())
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun navOut() {
        setFirstTime(this, false)
        val destination =
            intent.getSerializableExtra(AFTER_ONBOARDING_DONE_EXTRA) as AfterOnboardingAction
        Log.i("OnBoardingActivity", "onboarding finished")
        Log.i("OnBoardingActivity", "destination: $destination")
        when (destination) {
            AfterOnboardingAction.FINISH -> {
                setResult(RESULT_ONBOARDING_FINISHED)
                finish()
            }
        }
    }
}
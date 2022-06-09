package ch.epfl.sweng.rps.ui.settings

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.models.remote.Game
import ch.epfl.sweng.rps.models.remote.GameMode
import ch.epfl.sweng.rps.models.remote.Hand
import ch.epfl.sweng.rps.models.remote.Round
import ch.epfl.sweng.rps.remote.FirebaseReferences
import ch.epfl.sweng.rps.services.ProdServiceLocator
import ch.epfl.sweng.rps.services.ServiceLocator
import ch.epfl.sweng.rps.ui.onboarding.OnBoardingActivity
import ch.epfl.sweng.rps.utils.FirebaseEmulatorsUtils
import ch.epfl.sweng.rps.utils.L
import ch.epfl.sweng.rps.utils.dumpDebugInfos
import ch.epfl.sweng.rps.utils.openJsonFile
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class SettingsActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback,
    SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        private const val TITLE_TAG = "settingsActivityTitle"

        private fun applyTheme(
            themeKey: String,
            sharedPreferences: SharedPreferences
        ) {
            when (sharedPreferences.getString(themeKey, "system")) {
                "light" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                "dark" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

                "system" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }

                else -> {
                    L.of(SettingsActivity::class.java).e("Unknown theme: $themeKey")
                }
            }
        }

        fun applyTheme(context: Context, sharedPreferences: SharedPreferences? = null) {
            applyTheme(
                context.getString(R.string.theme_pref_key),
                sharedPreferences ?: PreferenceManager.getDefaultSharedPreferences(context)
            )
        }
    }

    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, HeaderFragment())
                .commit()
        } else {
            title = savedInstanceState.getCharSequence(TITLE_TAG)
        }
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                setTitle(R.string.title_activity_settings)
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, title)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.popBackStackImmediate()) {
            return true
        }
        finish()
        return true
    }


    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat, pref: Preference
    ): Boolean {
        // Instantiate the new Fragment
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader,
            pref.fragment!!
        ).apply {
            arguments = args
            setTargetFragment(caller, 0)
        }
        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings, fragment)
            .addToBackStack(null)
            .commit()
        title = pref.title
        return true
    }

    class HeaderFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.header_preferences, rootKey)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val button = findPreference<Preference>(getString(R.string.show_license_key))
            button?.setOnPreferenceClickListener {
                startActivity(Intent(view.context, OssLicensesMenuActivity::class.java))
                true
            }
            val uidPreference =
                findPreference<Preference>(getString(R.string.settings_show_uid_text))
            uidPreference?.setSummaryProvider {
                ServiceLocator.getInstance().repository.rawCurrentUid()
            }
            uidPreference?.setOnPreferenceClickListener {
                val clipboard: ClipboardManager? =
                    context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                val clip = ClipData.newPlainText(
                    "Copied uid",
                    ServiceLocator.getInstance().repository.rawCurrentUid()
                )
                clipboard?.setPrimaryClip(clip)
                true
            }
            val joinQueue = findPreference<Preference>(getString(R.string.join_queue_now_key))
            joinQueue?.setSummaryProvider {
                if (FirebaseEmulatorsUtils.emulatorUsed) "Emulator used" else "Firebase Emulator not used"
            }
            joinQueue?.setOnPreferenceClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    val tag = "Matchmaking"
                    val games = ServiceLocator.getInstance().repository.games.myActiveGames()
                    Log.w(tag, "games: $games")
                    if (games.isNotEmpty()) {
                        Toast.makeText(
                            context,
                            "You are already in a game (${games.first().id})",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }
                    Log.d(tag, "Joining queue")
                    try {
                        ServiceLocator.getInstance().matchmakingService.queue(
                            GameMode(
                                2,
                                GameMode.Type.PVP,
                                3,
                                0,
                                GameMode.GameEdition.RockPaperScissors
                            )
                        ).collect {
                            Log.i(tag, it.toString())
                        }
                    } catch (e: Exception) {
                        Log.e(tag, e.toString(), e)
                    }
                }
                true
            }
            findPreference<Preference>(getString(R.string.settings_clear_shared_prefs))?.setOnPreferenceClickListener {
                PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().clear()
                    .apply()
                // Show toast to confirm
                Toast.makeText(
                    requireContext(),
                    "Shared preferences cleared",
                    Toast.LENGTH_SHORT
                ).show()
                true
            }
            findPreference<Preference>(getString(R.string.settings_show_onboard))?.setOnPreferenceClickListener {
                OnBoardingActivity.createIntent(
                    requireActivity(),
                    onDone = OnBoardingActivity.AfterOnboardingAction.FINISH
                ).apply { startActivity(this) }
                true
            }

            findPreference<Preference>(getString(R.string.dump_sys_settings_key))?.setOnPreferenceClickListener {
                lifecycleScope.launch {
                    val f = dumpDebugInfos(requireContext())
                    openJsonFile(requireContext(), f)
                }
                true
            }

            val gameSettings =
                findPreference<Preference>(getString(R.string.add_artificial_game_settings))

            gameSettings!!.setOnPreferenceClickListener {
                val uid = ServiceLocator.getInstance().repository.getCurrentUid()
                val gameId = "artificial_game_1"
                val uid2 = "RquV8FkGInaPnyUnqncOZGJjSKJ3"
                val repo = ServiceLocator.getInstance()

                if (repo is ProdServiceLocator) {
                    lifecycleScope.launch { createArtificialGame(repo.firebaseReferences, gameId, uid, uid2) }
                }
                true
            }
        }

        companion object {
            suspend fun createArtificialGame(
                firebaseReferences: FirebaseReferences,
                gameId: String,
                uid: String,
                uid2: String
            ) {
                firebaseReferences.gamesCollection.document(gameId)
                    .set(
                        Game.Rps(
                            id = gameId,
                            players = listOf(
                                uid,
                                uid2
                            ),
                            rounds = mapOf(
                                "0" to Round.Rps(
                                    hands = mapOf(
                                        uid to Hand.PAPER,
                                        uid2 to Hand.ROCK
                                    ),
                                    timestamp = Timestamp.now()
                                )
                            ),
                            game_mode = GameMode(
                                playerCount = 2,
                                type = GameMode.Type.PVP,
                                rounds = 1,
                                timeLimit = 0,
                                edition = GameMode.GameEdition.RockPaperScissors
                            ).toGameModeString(),
                            current_round = 0,
                            done = true,
                            timestamp = Timestamp.now(),
                            player_count = 2
                        )
                    ).await()

            }
        }
    }

    override fun onResume() {
        super.onResume()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences?,
        key: String?
    ) {
        val themeKey = getString(R.string.theme_pref_key)
        if (key == themeKey) applyTheme(this, sharedPreferences)
    }
}
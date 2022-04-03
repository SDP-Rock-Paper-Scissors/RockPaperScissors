package ch.epfl.sweng.rps.ui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ch.epfl.sweng.rps.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}
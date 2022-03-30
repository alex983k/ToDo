package com.tomorrowit.todo.ui.prefs

import com.tomorrowit.todo.R
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class PrefsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(state: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs, rootKey)
    }
}
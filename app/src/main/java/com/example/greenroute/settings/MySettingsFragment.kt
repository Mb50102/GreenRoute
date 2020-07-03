package com.example.greenroute.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.greenroute.R

class MySettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }
}
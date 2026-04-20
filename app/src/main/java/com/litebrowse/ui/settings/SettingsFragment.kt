package com.litebrowse.ui.settings

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.litebrowse.LiteBrowseApp
import com.litebrowse.MainActivity
import com.litebrowse.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mozilla.geckoview.StorageController

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val activity = requireActivity() as MainActivity

        findPreference<ListPreference>("default_engine")?.setOnPreferenceChangeListener { _, newValue ->
            activity.searchEngineManager.currentEngineId = newValue as String
            true
        }

        findPreference<Preference>("clear_data")?.setOnPreferenceClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.clear_data_title)
                .setMessage(R.string.clear_data_message)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    val app = activity.application as LiteBrowseApp
                    CoroutineScope(Dispatchers.IO).launch {
                        app.database.historyDao().deleteAll()
                    }
                    app.geckoRuntime.storageController.clearData(
                        StorageController.ClearFlags.ALL
                    )
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
            true
        }
    }
}

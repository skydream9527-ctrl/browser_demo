package com.litebrowse.ui.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.litebrowse.LiteBrowseApp
import com.litebrowse.MainActivity
import com.litebrowse.R
import com.litebrowse.data.entity.Bookmark
import com.litebrowse.databinding.SheetMoreMenuBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mozilla.geckoview.GeckoSessionSettings

class MoreMenuSheet : BottomSheetDialogFragment() {

    private var _binding: SheetMoreMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SheetMoreMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        val app = activity.application as LiteBrowseApp
        val currentTab = activity.tabManager.currentTab.value

        binding.menuBookmark.setOnClickListener {
            currentTab?.let { tab ->
                CoroutineScope(Dispatchers.IO).launch {
                    app.database.bookmarkDao().insert(
                        Bookmark(title = tab.title, url = tab.url)
                    )
                }
            }
            dismiss()
        }

        binding.menuBookmarks.setOnClickListener {
            dismiss()
            findNavController().navigate(R.id.bookmarkFragment)
        }

        binding.menuHistory.setOnClickListener {
            dismiss()
            findNavController().navigate(R.id.historyFragment)
        }

        binding.menuShare.setOnClickListener {
            currentTab?.let { tab ->
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, tab.url)
                }
                startActivity(Intent.createChooser(intent, null))
            }
            dismiss()
        }

        binding.menuFind.setOnClickListener {
            dismiss()
            parentFragmentManager.setFragmentResult("show_find", Bundle.EMPTY)
        }

        binding.menuDesktop.setOnClickListener {
            currentTab?.let { tab ->
                val settings = tab.session.settings
                val currentMode = settings.userAgentMode
                settings.userAgentMode = if (currentMode == GeckoSessionSettings.USER_AGENT_MODE_MOBILE)
                    GeckoSessionSettings.USER_AGENT_MODE_DESKTOP
                else
                    GeckoSessionSettings.USER_AGENT_MODE_MOBILE
                tab.session.reload()
            }
            dismiss()
        }

        binding.menuSettings.setOnClickListener {
            dismiss()
            findNavController().navigate(R.id.settingsFragment)
        }

        binding.incognitoSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                activity.tabManager.addTab(isPrivate = true)
                dismiss()
                findNavController().navigate(R.id.homeFragment)
            }
        }

        binding.darkModeSwitch.isChecked =
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

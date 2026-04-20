package com.litebrowse.ui.browser

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.litebrowse.LiteBrowseApp
import com.litebrowse.MainActivity
import com.litebrowse.R
import com.litebrowse.data.entity.HistoryEntry
import com.litebrowse.databinding.FragmentBrowserBinding
import com.litebrowse.engine.TabInfo
import com.litebrowse.util.UrlUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSession.ContentDelegate
import org.mozilla.geckoview.GeckoSession.NavigationDelegate
import org.mozilla.geckoview.GeckoSession.ProgressDelegate
import org.mozilla.geckoview.AllowOrDeny
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.WebResponse

class BrowserFragment : Fragment() {

    private var _binding: FragmentBrowserBinding? = null
    private val binding get() = _binding!!
    private var currentTab: TabInfo? = null
    private var findInPageBar: FindInPageBar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBrowserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        val app = activity.application as LiteBrowseApp

        currentTab = activity.tabManager.currentTab.value
        if (currentTab == null) {
            val url = arguments?.getString("url")
            currentTab = activity.tabManager.addTab(url)
        }

        val tab = currentTab ?: return
        val session = tab.session

        binding.geckoView.setSession(session)

        // Navigation delegate
        session.navigationDelegate = object : NavigationDelegate {
            override fun onLocationChange(session: GeckoSession, url: String?,
                perms: List<GeckoSession.PermissionDelegate.ContentPermission>, hasUserGesture: Boolean) {
                url?.let {
                    tab.url = it
                    binding.urlText.text = UrlUtils.extractDomain(it)
                }
            }

            override fun onCanGoBack(session: GeckoSession, canGoBack: Boolean) {
                binding.navBack.alpha = if (canGoBack) 1f else 0.3f
                binding.navBack.isEnabled = canGoBack
            }

            override fun onCanGoForward(session: GeckoSession, canGoForward: Boolean) {
                binding.navForward.alpha = if (canGoForward) 1f else 0.3f
                binding.navForward.isEnabled = canGoForward
            }
        }

        // Content delegate
        session.contentDelegate = object : ContentDelegate {
            override fun onTitleChange(session: GeckoSession, title: String?) {
                title?.let { tab.title = it }
            }

            override fun onFullScreen(session: GeckoSession, fullScreen: Boolean) {
                if (fullScreen) {
                    binding.addressBar.visibility = View.GONE
                    binding.bottomNav.visibility = View.GONE
                    activity.window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
                } else {
                    binding.addressBar.visibility = View.VISIBLE
                    binding.bottomNav.visibility = View.VISIBLE
                    activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                }
            }

            override fun onExternalResponse(session: GeckoSession, response: WebResponse) {
                val uri = Uri.parse(response.uri)
                val request = DownloadManager.Request(uri).apply {
                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        uri.lastPathSegment ?: "download"
                    )
                }
                val dm = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                dm.enqueue(request)
            }
        }

        // Progress delegate
        session.progressDelegate = object : ProgressDelegate {
            override fun onPageStart(session: GeckoSession, url: String) {
                binding.progressBar.visibility = View.VISIBLE
            }

            override fun onPageStop(session: GeckoSession, success: Boolean) {
                binding.progressBar.visibility = View.GONE
                if (!tab.isPrivate && tab.url.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        app.database.historyDao().insert(
                            HistoryEntry(title = tab.title, url = tab.url)
                        )
                    }
                }
            }

            override fun onProgressChange(session: GeckoSession, progress: Int) {
                binding.progressBar.progress = progress
            }

            override fun onSecurityChange(session: GeckoSession,
                securityInfo: ProgressDelegate.SecurityInformation) {
                tab.isSecure = securityInfo.isSecure
                binding.lockIcon.visibility = if (securityInfo.isSecure) View.VISIBLE else View.GONE
            }
        }

        // Address bar
        binding.refreshButton.setOnClickListener { session.reload() }

        // Find in page
        findInPageBar = FindInPageBar(
            binding.findBar, binding.findInput, binding.findUp, binding.findDown, binding.findClose, session
        )

        // Listen for find-in-page request from MoreMenuSheet
        parentFragmentManager.setFragmentResultListener("show_find", viewLifecycleOwner) { _, _ ->
            findInPageBar?.show()
        }

        // Bottom nav
        binding.navHome.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
        binding.navBack.setOnClickListener { session.goBack() }
        binding.navTabs.setOnClickListener {
            findNavController().navigate(R.id.action_browser_to_tabs)
        }
        binding.navForward.setOnClickListener { session.goForward() }
        binding.navMore.setOnClickListener {
            com.litebrowse.ui.menu.MoreMenuSheet().show(childFragmentManager, "more_menu")
        }

        activity.tabManager.tabs.observe(viewLifecycleOwner) { tabs ->
            binding.tabCount.text = tabs.size.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

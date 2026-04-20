package com.litebrowse.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.litebrowse.LiteBrowseApp
import com.litebrowse.MainActivity
import com.litebrowse.R
import com.litebrowse.databinding.FragmentHomeBinding
import com.litebrowse.util.UrlUtils

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var engineChipAdapter: EngineChipAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        val app = activity.application as LiteBrowseApp

        // Search bar
        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchInput.text.toString().trim()
                if (query.isNotEmpty()) {
                    val url = if (UrlUtils.isUrl(query)) {
                        UrlUtils.ensureScheme(query)
                    } else {
                        activity.searchEngineManager.buildSearchUrl(query)
                    }
                    activity.tabManager.addTab(url)
                    findNavController().navigate(R.id.action_home_to_browser)
                }
                true
            } else false
        }

        // Engine icon click -> picker
        binding.engineIconText.setOnClickListener {
            EnginePickerSheet(activity.searchEngineManager) { engine ->
                activity.searchEngineManager.currentEngineId = engine.id
                updateEngineIcon()
                engineChipAdapter.notifyDataSetChanged()
            }.show(childFragmentManager, "engine_picker")
        }

        // Engine chips
        engineChipAdapter = EngineChipAdapter(activity.searchEngineManager) { engine ->
            activity.searchEngineManager.currentEngineId = engine.id
            updateEngineIcon()
        }
        binding.engineChips.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.engineChips.adapter = engineChipAdapter

        // Quick access grid
        val quickAccessAdapter = QuickAccessAdapter { item ->
            activity.tabManager.addTab(item.url)
            findNavController().navigate(R.id.action_home_to_browser)
        }
        binding.quickAccessGrid.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.quickAccessGrid.adapter = quickAccessAdapter

        app.database.quickAccessDao().getAll().asLiveData().observe(viewLifecycleOwner) { items ->
            quickAccessAdapter.submitList(items)
        }

        // Bottom nav
        setupBottomNav()
        updateEngineIcon()
    }

    private fun updateEngineIcon() {
        val engine = (requireActivity() as MainActivity).searchEngineManager.currentEngine
        binding.engineIconText.text = engine.iconLetter
    }

    private fun setupBottomNav() {
        val activity = requireActivity() as MainActivity
        binding.navHome.setOnClickListener { /* already on home */ }
        binding.navBack.setOnClickListener { /* no-op on home */ }
        binding.navTabs.setOnClickListener {
            findNavController().navigate(R.id.tabManagerFragment)
        }
        binding.navForward.setOnClickListener { /* no-op on home */ }
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

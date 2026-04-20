package com.litebrowse.ui.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.litebrowse.MainActivity
import com.litebrowse.R
import com.litebrowse.databinding.FragmentTabManagerBinding

class TabManagerFragment : Fragment() {

    private var _binding: FragmentTabManagerBinding? = null
    private val binding get() = _binding!!
    private var showPrivate = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTabManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        val tabManager = activity.tabManager

        binding.tabGrid.layoutManager = GridLayoutManager(requireContext(), 2)

        fun updateTabs() {
            val allTabs = tabManager.tabs.value.orEmpty()
            val filtered = allTabs.filter { it.isPrivate == showPrivate }
            val activeId = tabManager.currentTab.value?.id

            val adapter = TabCardAdapter(activeId,
                onTabClick = { tab ->
                    tabManager.switchTo(tab)
                    findNavController().navigate(R.id.browserFragment)
                },
                onCloseClick = { tab ->
                    tabManager.closeTab(tab)
                }
            )
            binding.tabGrid.adapter = adapter
            adapter.submitList(filtered)

            binding.normalChip.text = "普通标签 (${tabManager.normalTabCount})"
            binding.privateChip.text = "无痕标签 (${tabManager.privateTabCount})"
        }

        binding.normalChip.setOnClickListener {
            showPrivate = false
            updateChipState()
            updateTabs()
        }

        binding.privateChip.setOnClickListener {
            showPrivate = true
            updateChipState()
            updateTabs()
        }

        binding.closeAll.setOnClickListener {
            tabManager.closeAllTabs(privateOnly = showPrivate)
        }

        binding.addTab.setOnClickListener {
            tabManager.addTab(isPrivate = showPrivate)
            findNavController().navigate(R.id.homeFragment)
        }

        tabManager.tabs.observe(viewLifecycleOwner) { updateTabs() }
        updateChipState()
    }

    private fun updateChipState() {
        binding.normalChip.setBackgroundResource(
            if (!showPrivate) R.drawable.bg_engine_chip_selected else R.drawable.bg_engine_chip
        )
        binding.privateChip.setBackgroundResource(
            if (showPrivate) R.drawable.bg_engine_chip_selected else R.drawable.bg_engine_chip
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

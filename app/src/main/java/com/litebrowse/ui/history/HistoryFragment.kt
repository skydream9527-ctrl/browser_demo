package com.litebrowse.ui.history

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.litebrowse.LiteBrowseApp
import com.litebrowse.MainActivity
import com.litebrowse.R
import com.litebrowse.databinding.FragmentHistoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = (requireActivity().application) as LiteBrowseApp
        val dao = app.database.historyDao()

        binding.historyList.layoutManager = LinearLayoutManager(requireContext())

        val adapter = HistoryAdapter { entry ->
            (requireActivity() as MainActivity).tabManager.addTab(entry.url)
            findNavController().navigate(R.id.browserFragment)
        }
        binding.historyList.adapter = adapter

        // Observe all history
        dao.getAll().asLiveData().observe(viewLifecycleOwner) { entries ->
            adapter.submitEntries(entries)
            binding.emptyText.visibility = if (entries.isEmpty()) View.VISIBLE else View.GONE
        }

        // Search
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim() ?: ""
                val flow = if (query.isEmpty()) dao.getAll() else dao.search(query)
                flow.asLiveData().observe(viewLifecycleOwner) { entries ->
                    adapter.submitEntries(entries)
                    binding.emptyText.visibility = if (entries.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        })

        // Clear all
        binding.clearAll.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.clear_all)
                .setMessage("确定清除所有历史记录？")
                .setPositiveButton(R.string.confirm) { _, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dao.deleteAll()
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.litebrowse.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.litebrowse.data.entity.HistoryEntry
import com.litebrowse.databinding.ItemHistoryBinding
import com.litebrowse.databinding.ItemHistoryHeaderBinding
import com.litebrowse.util.UrlUtils
import com.litebrowse.util.toDateGroup
import com.litebrowse.util.toTimeString

class HistoryAdapter(
    private val onClick: (HistoryEntry) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Any> = emptyList() // String (header) or HistoryEntry

    fun submitEntries(entries: List<HistoryEntry>) {
        val grouped = mutableListOf<Any>()
        var lastGroup = ""
        for (entry in entries) {
            val group = entry.visitedAt.toDateGroup()
            if (group != lastGroup) {
                grouped.add(group)
                lastGroup = group
            }
            grouped.add(entry)
        }
        items = grouped
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is String) TYPE_HEADER else TYPE_ENTRY
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            HeaderVH(ItemHistoryHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            EntryVH(ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderVH -> holder.binding.headerText.text = items[position] as String
            is EntryVH -> {
                val entry = items[position] as HistoryEntry
                holder.binding.historyTitle.text = entry.title.ifEmpty { entry.url }
                holder.binding.historyUrl.text = UrlUtils.extractDomain(entry.url)
                holder.binding.historyIcon.text = entry.title.firstOrNull()?.toString() ?: "?"
                holder.binding.historyTime.text = entry.visitedAt.toTimeString()
                holder.binding.root.setOnClickListener { onClick(entry) }
            }
        }
    }

    class HeaderVH(val binding: ItemHistoryHeaderBinding) : RecyclerView.ViewHolder(binding.root)
    class EntryVH(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ENTRY = 1
    }
}

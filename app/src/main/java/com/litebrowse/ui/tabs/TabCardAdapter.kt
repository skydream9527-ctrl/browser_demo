package com.litebrowse.ui.tabs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.litebrowse.R
import com.litebrowse.databinding.ItemTabCardBinding
import com.litebrowse.engine.TabInfo
import com.litebrowse.util.UrlUtils

class TabCardAdapter(
    private val activeTabId: String?,
    private val onTabClick: (TabInfo) -> Unit,
    private val onCloseClick: (TabInfo) -> Unit
) : ListAdapter<TabInfo, TabCardAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(val binding: ItemTabCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tab: TabInfo) {
            binding.tabTitle.text = tab.title.ifEmpty { "新标签页" }
            binding.tabDomain.text = UrlUtils.extractDomain(tab.url)
            binding.root.setBackgroundResource(
                if (tab.id == activeTabId) R.drawable.bg_tab_card_active
                else R.drawable.bg_tab_card
            )
            binding.root.setOnClickListener { onTabClick(tab) }
            binding.closeButton.setOnClickListener { onCloseClick(tab) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTabCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<TabInfo>() {
            override fun areItemsTheSame(old: TabInfo, new: TabInfo) = old.id == new.id
            override fun areContentsTheSame(old: TabInfo, new: TabInfo) = old == new
        }
    }
}

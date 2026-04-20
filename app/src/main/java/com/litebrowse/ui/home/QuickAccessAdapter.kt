package com.litebrowse.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.litebrowse.data.entity.QuickAccess
import com.litebrowse.databinding.ItemQuickAccessBinding

class QuickAccessAdapter(
    private val onClick: (QuickAccess) -> Unit
) : ListAdapter<QuickAccess, QuickAccessAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(val binding: ItemQuickAccessBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QuickAccess) {
            binding.quickIcon.text = item.iconEmoji
            binding.quickTitle.text = item.title
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuickAccessBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<QuickAccess>() {
            override fun areItemsTheSame(old: QuickAccess, new: QuickAccess) = old.id == new.id
            override fun areContentsTheSame(old: QuickAccess, new: QuickAccess) = old == new
        }
    }
}

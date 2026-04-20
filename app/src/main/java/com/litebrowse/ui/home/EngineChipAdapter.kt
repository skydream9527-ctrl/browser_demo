package com.litebrowse.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.litebrowse.R
import com.litebrowse.databinding.ItemEngineChipBinding
import com.litebrowse.engine.SearchEngine
import com.litebrowse.engine.SearchEngineManager

class EngineChipAdapter(
    private val manager: SearchEngineManager,
    private val onClick: (SearchEngine) -> Unit
) : RecyclerView.Adapter<EngineChipAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemEngineChipBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEngineChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val engine = manager.engines[position]
        holder.binding.chipText.text = engine.name
        holder.binding.chipText.setBackgroundResource(
            if (engine.id == manager.currentEngineId) R.drawable.bg_engine_chip_selected
            else R.drawable.bg_engine_chip
        )
        if (engine.id == manager.currentEngineId) {
            holder.binding.chipText.setTextColor(holder.itemView.context.getColor(android.R.color.white))
        } else {
            holder.binding.chipText.setTextColor(
                com.google.android.material.color.MaterialColors.getColor(
                    holder.itemView, com.google.android.material.R.attr.colorOnSurfaceVariant
                )
            )
        }
        holder.binding.root.setOnClickListener {
            onClick(engine)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = manager.engines.size
}
